//
// Implementation of the HabitsAdapter class
// This class provides an interface to show comparison between spendings in current and previous month
//
package com.example.personalfinance;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.ReportViewHolder>  {

    /**/
    /*
    * NAME
        HabitsAdapter::HabitsAdapter() - Constructor creates a new instance of category report

    * SYNOPSIS
        public HabitsAdapter::HabitsAdapter();

    * DESCRIPTION
        The constructor attempts to create a category report for each of the 10 categories. Then, it
        stores each report object in the m_Categories list.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:30pm, 09/11/2021
    */
    /**/
    public HabitsAdapter(){
        for (String a_Category: Util.GetExistingCategories()){
            m_Categories.add(new CategoryReport(a_Category));
        }
    } /* public HabitsAdapter() */

    /**/
    /*
    * NAME
        HabitsAdapter::onCreateViewHolder() - Inflates layout with the layout for displaying the comparison

    * SYNOPSIS
        HabitsAdapter.ReportViewHolder HabitsAdapter::onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
        * parent => view group into which the new view will be added after it is bound to an adapter position
        * viewType => type of the new view

    * DESCRIPTION
        This function will attempt to display each category in the formatting specified by activity_category layout

    * RETURNS
        Returns a new ViewHolder that holds a View of the category view type.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:30pm, 09/11/2021
    */
    /**/
    @NonNull
    @NotNull
    @Override
    public HabitsAdapter.ReportViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_retrieve_habits, parent, false);
        return new ReportViewHolder(view);
    } /* public HabitsAdapter.ReportViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) */


    /**/
    /*
    * NAME
        HabitsAdapter::onBindHolder() - Updates layout to show spending comparison for each category

    * SYNOPSIS
        void HabitsAdapter::onBindViewHolder(@NonNull @NotNull ExpenseViewHolder holder, int position);
        *  holder => the view for the page that contains metadata for a budget (category name,
           category icon, budget, expenses, number of transactions)
        * position => latest item in the adapter

    * DESCRIPTION
        This function populates values for each category with the category report object. Once a
        category is clicked, it is expanded to show a bar chart. On clicking on an expanded category,
        it is closed.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:40pm, 09/11/2021
    */
    /**/
    @Override
    public void onBindViewHolder(@NonNull @NotNull ReportViewHolder holder, int position) {
        CategoryReport Category = m_Categories.get(position);
        String category=Category.GetCategory();
        holder.SetCategoryName(category);
        holder.SetCategoryImage(category);
        holder.Bind(Category);

        holder.itemView.setOnClickListener(v -> {
            Log.i("Click","clicked");
            boolean expanded = Category.isExpanded();
            Category.SetExpanded(!expanded);
            notifyItemChanged(position);
        });
    } /*  public void onBindViewHolder(@NonNull @NotNull ReportViewHolder holder, int position) */

    /**/
    /*
    * NAME
        HabitsAdapter::getItemCount() - Overrides the getItemCount() to return the value of the category list

    * SYNOPSIS
        int HabitsAdapter::getItemCount();

    * DESCRIPTION
        This function returns the size of the list of categories

    * RETURNS
        Returns the size of m_Categories, which has a list of all categories

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:50pm, 09/11/2021
    */
    /**/
    @Override
    public int getItemCount() {
        return m_Categories.size();
    }


    //
    // Implementation of the HabitsAdapter class
    //
    // This is inside the Adapter class so that adapters can use the views
    // from this class and update them.
    public static class ReportViewHolder extends RecyclerView.ViewHolder {

        View m_SubItem;
        ImageView m_Arrow;

        // default constructor to set the itemView as the rootview
        public ReportViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_SubItem=itemView.findViewById(R.id.categoryReport);
            m_Arrow=itemView.findViewById(R.id.forwardArrow);
        }

        /**/
        /*
        * NAME
            HabitsAdapter::SetCategoryName() - Sets category name

        * SYNOPSIS
            void HabitsAdapter::SetCategoryName(a_CategoryName);
            * a_CategoryName => the category name to be assigned to the model

        * DESCRIPTION
            This function will attempt to set the passed category name to the model.
            Since null entry would have already been checked for before the function call,
            this simply extracts the value and assigns it to the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            8:00pm, 09/12/2021
        */
        /**/
        private void SetCategoryName(String a_CategoryName){
            TextView categoryName = itemView.findViewById(R.id.categoryName);
            categoryName.setText("Spending in: " + a_CategoryName);
        } /*  private void SetCategoryName(String a_CategoryName) */


        /**/
        /*
        * NAME
            HabitsAdapter::SetCategoryImage() - Sets category image

        * SYNOPSIS
            void HabitsAdapter::SetCategoryImage(a_CategoryName);
            * a_CategoryName => the category name to identify the image to be assigned to the model

        * DESCRIPTION
            This function will use the passed category name to set up a category icon for the view.
            A call to SetCategoryIcon() in the Utils class returns an image id, which is then used by
            this function to set the image for the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            8:00pm, 09/12/2021
        */
        /**/
        private void SetCategoryImage(String a_CategoryName){
            int imageId = Util.SetCategoryIcon(a_CategoryName);
            ImageView categoryImage = itemView.findViewById(R.id.categoryIcon);
            categoryImage.setImageResource(imageId);
        } /* private void SetCategoryImage(String a_CategoryName) */


        /**/
        /*
        * NAME
            HabitsAdapter::Bind() - Listen for category click and display chart

        * SYNOPSIS
            void HabitsAdapter::Bind(CategoryReport a_Category);
            * a_Category => the category name to identify the report

        * DESCRIPTION
            This function will use the passed category name to check if it has been expanded. If the
            model view is already expanded, it will close it.
            If not, it will expand the view, and call FetchData() to fetch data for current and
            previous month. Then, it will load the habits chart with the data

        * AUTHOR
            Shreeti Shrestha

        * DATE
            9:00pm, 09/12/2021
        */
        /**/
        public void Bind(CategoryReport a_Category) {
            boolean expanded = a_Category.isExpanded();
            m_SubItem.setVisibility(expanded?View.VISIBLE:View.GONE);
            m_Arrow.setImageResource(expanded?R.drawable.ic_baseline_keyboard_arrow_down_24:R.drawable.ic_baseline_keyboard_arrow_right_24);
            FetchData(a_Category);

        }/* public void Bind(CategoryReport a_Category)  */

        /**/
        /*
        * NAME
            HabitsAdapter::FetchData() - Get Expense data from database

        * SYNOPSIS
            void HabitsAdapter::FetchData(CategoryReport a_Category);
            * a_Category => a CategoryReport object for a specified category

        * DESCRIPTION
            For the passed CategoryReport object, this function fetches expense data for past and current
            month from the database. Then, it adds all the expenses to get a total amount for each month,
            which is used to set the amount for the CategoryReport object

        * AUTHOR
            Shreeti Shrestha

        * DATE
            10:00pm, 09/12/2021
        */
        /**/
        private void FetchData(CategoryReport a_Category) {

            MutableDateTime epoch = new MutableDateTime();
            epoch.setDate(0);

            DateTime now = new DateTime();
            int today = now.getDayOfMonth();

            DateTime prevMonthDay = new DateTime().minusMonths(1).withDayOfMonth(today);
            DateTime prevMonthFirstDay = new DateTime().minusMonths(1).withDayOfMonth(1);
            Days prevMonthFirstCount = Days.daysBetween(epoch,prevMonthFirstDay);
            Days prevMonthDayCount = Days.daysBetween(epoch, prevMonthDay);

            DateTime currentMonthDay = new DateTime().withDayOfMonth(today);
            DateTime currentMonthFirstDay = new DateTime().withDayOfMonth(1);
            Days currentMonthFirstCount = Days.daysBetween(epoch,currentMonthFirstDay);
            Days currentMonthDayCount = Days.daysBetween(epoch, currentMonthDay);

            Util.GetExpenseReference().orderByChild("day").startAt(currentMonthFirstCount.getDays())
                    .endAt(currentMonthDayCount.getDays())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Double a_CurrentTotal = 0.0;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Data d = data.getValue(Data.class);
                                assert d != null;
                                if (!(d.GetCategory().equals(a_Category.GetCategory()))) {
                                    continue;
                                }
                                if (d.GetAmount() > 0) {
                                    a_CurrentTotal += d.GetAmount();
                                }
                            }
                            a_Category.setCurrentAmount(a_CurrentTotal);

                    Util.GetExpenseReference().orderByChild("day")
                            .startAt(prevMonthFirstCount.getDays())
                            .endAt(prevMonthDayCount.getDays())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Double a_PrevTotal = 0.0;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Data d = data.getValue(Data.class);
                                assert d != null;
                                Log.i("Previous month",String.valueOf(d.GetMonth()));
                                if(!(d.GetCategory().equals(a_Category.GetCategory()))){
                                    continue;
                                }
                                    if (d.GetAmount() > 0) {
                                        a_PrevTotal += d.GetAmount();
                                }
                            }
                            a_Category.setPreviousAmount(a_PrevTotal);
                            BarChart barChart=itemView.findViewById(R.id.categoryBarChart);
                            HabitChart habitChart = new HabitChart(barChart);
                            if(a_Category.getPreviousAmount()==0 && a_Category.getCurrentAmount()==0){
                                barChart.setNoDataText("No data available for comparison");
                                return;
                            }
                            habitChart.SetUpBarChart();
                            habitChart.LoadBarChart(a_Category);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            Log.e("Error fetching previous month's data",
                                    error.getMessage(),error.toException());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.e("Error fetching this month's data",error.getMessage(),
                            error.toException());
                }
            });
        }
    }/*   private void FetchData(CategoryReport a_Category) */
    private final List<CategoryReport> m_Categories = new ArrayList<>();

}
