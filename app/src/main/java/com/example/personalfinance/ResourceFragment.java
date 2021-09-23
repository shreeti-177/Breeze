package com.example.personalfinance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plaid.internal.core.protos.link.workflow.nodes.panes.ButtonWithCardsPane;

public class ResourceFragment extends Fragment {

    View m_RootView;
    public ResourceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_RootView=inflater.inflate(R.layout.fragment_resource, container, false);
        CardView resource1 = m_RootView.findViewById(R.id.khanAcademy);
        CardView resource2 = m_RootView.findViewById(R.id.cnnMoney);
        CardView resource3 = m_RootView.findViewById(R.id.yahoo);

        ImageView resource1Img = m_RootView.findViewById(R.id.khanAcademyImg);
        ImageView resource2Img = m_RootView.findViewById(R.id.cnnMoneyImg);
        ImageView resource3Img = m_RootView.findViewById(R.id.yahooImg);

        TextView resource1Txt = m_RootView.findViewById(R.id.khanAcademyText);
        TextView resource2Txt = m_RootView.findViewById(R.id.cnnMoneyText);
        TextView resource3Txt = m_RootView.findViewById(R.id.yahooText);

        resource1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://www.khanacademy.org/economics-finance-domain/core-finance");
            }
        });

        resource1Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://www.khanacademy.org/economics-finance-domain/core-finance");
            }
        });

        resource1Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://www.khanacademy.org/economics-finance-domain/core-finance");
            }
        });
        resource2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://money.cnn.com/pf/money-essentials");
            }
        });
        resource2Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://money.cnn.com/pf/money-essentials");
            }
        });
        resource2Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://money.cnn.com/pf/money-essentials");
            }
        });
        resource3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://finance.yahoo.com/");
            }
        });
        resource3Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://finance.yahoo.com/");
            }
        });
        resource3Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateTo("https://finance.yahoo.com/");
            }
        });
        return m_RootView;
    }

    private void NavigateTo(String a_Uri){
        Uri uri = Uri.parse(a_Uri);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}