package com.example.personalfinance;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plaid.client.model.Transaction;
import com.plaid.client.model.TransactionsGetRequest;
import com.plaid.client.model.TransactionsGetResponse;
import com.plaid.client.request.PlaidApi;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Months;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{
    private List<Data> m_Transactions = new ArrayList<>();
    private final String TAG="HomeActivity";

    public TransactionAdapter(){}
    public void SetTransactions(List<Data> a_Transactions){
        m_Transactions=a_Transactions;
    }

    @NonNull
    @NotNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_recent_transactions, parent, false);
        return new TransactionAdapter.TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TransactionAdapter.TransactionViewHolder holder, int position) {
        Log.i("Position",String.valueOf(position));

        Data key = m_Transactions.get(m_Transactions.size()-(position+1));
//        Log.i("Here",String.valueOf(key.getMerchant()));
        TextView a_MerchantName = holder.m_MerchantField;
        TextView a_DateField = holder.m_DateField;
        TextView a_AmountField = holder.m_AmountField;

        String a_Merchant = key.getMerchant();
        String a_Date = key.getDate();
        Double a_Amount = key.getAmount();

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

    public class TransactionViewHolder extends RecyclerView.ViewHolder{
        TextView m_MerchantField;
        TextView m_DateField;
        TextView m_AmountField;
        public LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);


        public TransactionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_MerchantField=itemView.findViewById(R.id.merchantField);
            m_DateField=itemView.findViewById(R.id.dateField);
            m_AmountField=itemView.findViewById(R.id.amountField);
        }
    }


}