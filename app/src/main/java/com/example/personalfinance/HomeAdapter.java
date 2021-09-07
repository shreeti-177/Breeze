package com.example.personalfinance;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomePageViewHolder> {
    private List<Data> m_Transactions = new ArrayList<>();
    private final String TAG="HomeActivity";


    public HomeAdapter(){}
    public void SetTransactions(List<Data> a_Transactions){
        m_Transactions=a_Transactions;
    }
    @NonNull
    @NotNull
    @Override
    public HomeAdapter.HomePageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_home_page, parent, false);
        return new HomeAdapter.HomePageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeAdapter.HomePageViewHolder holder, int position) {
        Log.i("Position",String.valueOf(position));

        Data key = m_Transactions.get(m_Transactions.size()-(position+1));
//        Log.i("Here",String.valueOf(key.getMerchant()));
        TextView a_MerchantName = holder.m_MerchantField;
        TextView a_DateField = holder.m_DateField;
        TextView a_AmountField = holder.m_AmountField;

        String a_Merchant = key.getMerchant();
        String a_Date = key.getDate();
        Double a_Amount = key.getAmount();
        Log.i("Amount",String.valueOf(a_Amount));

        if (a_Merchant==null||a_Date==null||a_Amount==null){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(holder.params);
        }
        a_MerchantName.setText(String.valueOf(a_Merchant));
        a_DateField.setText(String.valueOf(a_Date));
        if(a_Amount<0){
            a_AmountField.setTextColor(Color.parseColor("#C41E3A"));
            a_AmountField.setText(String.valueOf(a_Amount));
        }
        else{
            a_AmountField.setTextColor(Color.parseColor("#6FCF97"));
            a_AmountField.setText("+" + String.valueOf(a_Amount));
        }

    }

    @Override
    public int getItemCount() {
        return m_Transactions.size();
    }

    public class HomePageViewHolder extends RecyclerView.ViewHolder{
        TextView m_UserNameField;
        TextView m_MerchantField;
        TextView m_DateField;
        TextView m_AmountField;
        public LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);

        public HomePageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_MerchantField=itemView.findViewById(R.id.merchantField);
            m_DateField=itemView.findViewById(R.id.dateField);
            m_AmountField=itemView.findViewById(R.id.amountField);
            m_UserNameField=itemView.findViewById(R.id.userNameField);

        }
    }


}
