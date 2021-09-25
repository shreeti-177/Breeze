//
// Implementation of the Home Fragment class
//
package com.example.personalfinance;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    // default onCreate function with a previously saved instance
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**/
    /*
    * NAME
        HomeFragment::onCreateView() - Overrides the default onCreateView function for a fragment

    * SYNOPSIS
        void HomeFragment::onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);
        * inflater => inflater used to instantiate fragment_home layout XML to view objects
        * container => group that contains children views
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the default home page fragment.
        It will include the toolbar and the bottom navigation bar.
        Then, it will add a dashboard chart to show monthly expenses.
        In the bottom half, it will show all transactions (debit and credit) till date.
        If there's no expenses, it will note it in the screen.
        It will also provide options to set budget and set new goals.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        12:37pm, 02/04/2021
    */
    /**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        //Inflate the layout for this fragment
        m_RootView = inflater.inflate(R.layout.fragment_home, container, false);

        //Execute update processes in the background (to fetch any new information from Plaid)
        Util.m_Executor.execute(BackgroundTasks::UpdateOnlineTransactions);

        //AddListeners for events
        ListenForEvents();
        return m_RootView;
    } /*void HomeFragment::onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState);*/


    /**/
    /*
    * NAME
        HomeFragment::ListenForEvents() - Sets OnClick Listeners for all events in the home fragment

    * SYNOPSIS
        HomeFragment::private void ListenForEvents();

    * DESCRIPTION
        This function will attempt to set listeners for the following events:
        * Open activity to set budget when user clicks the Set Budget Icon
        * Open activity to set goals when user click the Set Goals Icon
        * Refresh the dashboard and transactions once new data is returned

    * AUTHOR
        Shreeti Shrestha

    * DATE
        12:37pm, 05/04/2021
    */
    /**/
    private void ListenForEvents(){
        ImageButton m_AddBudgetBtn = m_RootView.findViewById(R.id.myBudget);
        ImageButton m_AddGoalsBtn = m_RootView.findViewById(R.id.myPlans);

        //Start new activity on button clicks for setting budget and goals
        m_AddBudgetBtn.setOnClickListener(v -> startActivity(new Intent(getContext(),
                BudgetActivity.class)));

        m_AddGoalsBtn.setOnClickListener(v -> startActivity(new Intent(getContext(),
                PlansActivity.class)));

        //Display current user's name in Home screen
        TextView m_UserName = m_RootView.findViewById(R.id.userNameField);
        m_UserName.setText(Objects.requireNonNull(Objects.requireNonNull(Util.m_Auth
                .getCurrentUser()).getDisplayName()).split(" ")[0]);

        DisplayDashboard();

        //Set Up Recycler View for Recent Transactions
        m_TransactionsText = m_RootView.findViewById(R.id.transactionsText);
        m_HomePageView = m_RootView.findViewById(R.id.homePage);
        m_HomePageView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        m_HomeAdapter = new HomeAdapter();
        m_HomePageView.setAdapter(m_HomeAdapter);

        DisplayTransactions();
        m_RootView.setVisibility(View.VISIBLE);
    } /* HomeFragment::private void ListenForEvents(); */


    /**/
    /*
    * NAME
        HomeFragment::DisplayTransactions() - Displays all transactions in descending order by date

    * SYNOPSIS
        void HomeFragment::DisplayTransactions();

    * DESCRIPTION
        This function will attempt to fetch all transactions (debit and credit) from Firebase
        and display them on screen. Since the data keeps changing, it sets up an adapter to
        constantly listen to any data changes and update the list periodically. In the event that
        it's a new user and there are no transactions, it will display a message to notify the user.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        12:50pm, 05/04/2021
    */
    /**/
    private void DisplayTransactions(){

        //Read transaction data from Firebase
        //Order by "day" to get transactions in ascending order
        m_ExpenseRef.orderByChild("day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    assert data != null;
                    Double a_Amount = data.GetAmount();

                    //Disclude data missing merchant name, date or amount
                    if (data.GetMerchant()==null||data.GetDate()==null||a_Amount==null){
                        continue;
                    }
                    m_AllTransactions.add(data);
                }

                //if transactions list is empty, collapse section for transactions
                if(m_AllTransactions.size()==0){
                    m_TransactionsText.setText("Your expenses will be listed here");
                    m_TransactionsText.setTextSize(16f);
                    m_HomePageView.setVisibility(GONE);
                }

                //Dataset changed due to added transactions
                m_HomeAdapter.notifyDataSetChanged();

                //Update recycler view with new transactions
                m_HomeAdapter.SetTransactions(m_AllTransactions);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.i("Error retrieving expense data",error.getMessage(), error.toException());
            }
        });
    }   /* private void DisplayTransactions() */


    /**/
    /*
    * NAME
        HomeActivity::DisplayDashboard() - Displays monthly expenses in a chart

    * SYNOPSIS
        void HomeFragment::DisplayDashboard();

    * DESCRIPTION
        This function will attempt to fetch monthly expenses from Firebase for the past 6 months
        and build a line chart with the data. In the event that it's a new user and there are no
        expense summaries, it will display a message to notify the user.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        1:15pm, 02/05/2021
    */
    /**/

    private void DisplayDashboard(){

        LineChart chart = m_RootView.findViewById(R.id.dashboardChart);

        //Clear summaryList to fetch updated data
        m_SummaryList.clear();

        //Fetch Summary Data from Firebase
        m_SummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Summary a_Summary = dataSnapshot.getValue(Summary.class);
                    Log.i("Here","here");
                    assert a_Summary != null;
                    Log.i("Summary expense",a_Summary.getExpense().toString());
                    m_SummaryList.add(a_Summary);
                }

                //If no summaries till date, don't load dashboard
                if(m_SummaryList.size()<2){
                    Log.i("No chart data","No summaries to show");
                    chart.setNoDataText("No Expenses till date!");
                    return;
                }

                Dashboard m_Dashboard = new Dashboard(chart);
                m_Dashboard.SetUpLineChart("Monthly Expenses");
                m_Dashboard.LoadLineChart(m_SummaryList);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Error fetching summary data",error.getMessage(),error.toException());
            }
        });
    }   /* private void DisplayDashboard() */

    private HomeAdapter m_HomeAdapter;
    private View m_RootView;
    private RecyclerView m_HomePageView;
    private TextView m_TransactionsText;
    private final List<Data> m_AllTransactions = new ArrayList<>();
    private final List<Summary> m_SummaryList=new ArrayList<>();

    private final DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference()
            .child("expenses").child(Util.getUid());
    private final DatabaseReference m_SummaryRef = FirebaseDatabase.getInstance().getReference()
            .child("summary").child(Util.getUid());
}