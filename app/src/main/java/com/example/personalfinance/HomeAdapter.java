//
// Implementation of the Home Adapter class
//
package com.example.personalfinance;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomePageViewHolder> {

    public HomeAdapter(){
        //Required empty constructor
    }

    /**/
    /*
    * NAME
        HomeAdapter::SetTransactions() - Sets up a new list of transactions for display

    * SYNOPSIS
        void HomeAdapter::SetTransactions(List<Data> a_Transactions);
        *   a_Transactions => new list that has all recent transactions

    * DESCRIPTION
        This function will attempt to refresh the transactions list with a new and updated list
        passed from Firebase. Since the HomeFragment checks the size of the list to be greater than 0,
        an empty list will not be passed to this function.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        1:25pm, 02/02/2021
    */
    /**/
    public void SetTransactions(List<Data> a_Transactions){
        m_Transactions=a_Transactions;
    }/* public void SetTransactions(List<Data> a_Transactions) */


    //Override the HomeAdapter to display the retrieve transactions layout
    @NonNull
    @NotNull
    @Override
    public HomeAdapter.HomePageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //Inflate the layout for this adapter
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_transactions, parent, false);
        return new HomePageViewHolder(view);
    }


    /**/
    /*
    * NAME
        HomeAdapter::onBindViewHolder() - Overrides the default onBindViewHolder to update the
        recycler view and show all recent transactions

    * SYNOPSIS
        void HomeAdapter::onBindViewHolder(@NonNull @NotNull HomeAdapter.HomePageViewHolder holder,
                          int position);
        * holder => the view for the page that contains metadata for a transaction (merhcant name,
          date, amount)
        * position => latest item in the adapter

    * DESCRIPTION
        This function will attempt to bind all the transactions and display them in the recycler
        view one by one. If there are any null values, it will skip that transaction.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        1:25pm, 02/05/2021
    */
    /**/
    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeAdapter.HomePageViewHolder holder, int position) {

        TextView merchantName = holder.m_MerchantField;
        TextView dateField = holder.m_DateField;
        TextView amountField = holder.m_AmountField;
        ImageView categoryField = holder.m_CategoryField;

        Data key = m_Transactions.get(m_Transactions.size()-(position+1));

        String merchant = key.GetMerchant();
        String date = key.GetDate();
        Double amount = key.GetAmount();
        String category = key.GetCategory();

        // Ignore transaction if there are null values
        if (merchant==null||date==null||amount==null){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(holder.params);
        }

        merchantName.setText(String.valueOf(merchant));
        dateField.setText(String.valueOf(date));

        // If transaction is a credit, display amount in green.
        // Otherwise, display amount in red.
        if(amount<0){
            amountField.setTextColor(Color.parseColor("#6FCF97"));
            amountField.setText(String.valueOf(-(amount)));
        }
        else{
            amountField.setTextColor(Color.parseColor("#C41E3A"));
            amountField.setText("-" + amount);
        }

        // Set category tag for each transaction
        categoryField.setImageResource(Util.SetCategoryIcon(category));
    } /* public void onBindViewHolder(@NonNull @NotNull HomeAdapter.HomePageViewHolder holder, int position) */

    @Override
    public int getItemCount() {
        return m_Transactions.size();
    }

    //
    // Implementation of the HomePageViewHolder class
    //
    // This is inside the Adapter class so that adapters can use the views
    // from this class and update them.
    public static class HomePageViewHolder extends RecyclerView.ViewHolder{

        //Assign views by id from the resource page
        public HomePageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_MerchantField=itemView.findViewById(R.id.merchantField);
            m_DateField=itemView.findViewById(R.id.dateField);
            m_AmountField=itemView.findViewById(R.id.amountField);
            m_CategoryField=itemView.findViewById(R.id.categoryTag);
        }

        private final TextView m_MerchantField;
        private final TextView m_DateField;
        private final TextView m_AmountField;
        private final ImageView m_CategoryField;
        private final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
    }

    private List<Data> m_Transactions = new ArrayList<>();

}
