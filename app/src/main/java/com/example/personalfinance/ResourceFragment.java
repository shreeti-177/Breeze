//
// Implementation of the ResourceFragment class
// This class sets up an interface for displaying helpful resources in personal finance
//
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

public class ResourceFragment extends Fragment {

    public ResourceFragment() {
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
        ResourceFragment::onCreateView() - Overrides the default onCreateView function for a fragment

    * SYNOPSIS
        View ResourceFragment::onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);
        * inflater => inflater used to instantiate fragment_resource layout XML to view objects
        * container => group that contains children views
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the default resource fragment page.
        It will include 3 resources: khanAcademy, cnnMoney, yahoo with their icons and a short description
        Then, it will set up onclick listeners for each resource icon/description
        Upon clicking either of the resource, the user is then directed to an external link

    * RETURNS
        Returns the rootview for the fragment

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:37pm, 09/04/2021
    */
    /**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_RootView=inflater.inflate(R.layout.fragment_resource, container, false);
        m_Resource1 = m_RootView.findViewById(R.id.khanAcademy);
        m_Resource2 = m_RootView.findViewById(R.id.cnnMoney);
        m_Resource3 = m_RootView.findViewById(R.id.yahoo);

        ImageView resource1Img = m_RootView.findViewById(R.id.khanAcademyImg);
        ImageView resource2Img = m_RootView.findViewById(R.id.cnnMoneyImg);
        ImageView resource3Img = m_RootView.findViewById(R.id.yahooImg);

        TextView resource1Txt = m_RootView.findViewById(R.id.khanAcademyText);
        TextView resource2Txt = m_RootView.findViewById(R.id.cnnMoneyText);
        TextView resource3Txt = m_RootView.findViewById(R.id.yahooText);


        m_Resource1.setOnClickListener(v -> NavigateTo("https://www.khanacademy.org/economics-finance-domain/core-finance"));
        resource1Img.setOnClickListener(v -> NavigateTo("https://www.khanacademy.org/economics-finance-domain/core-finance"));
        resource1Txt.setOnClickListener(v -> NavigateTo("https://www.khanacademy.org/economics-finance-domain/core-finance"));

        m_Resource2.setOnClickListener(v -> NavigateTo("https://money.cnn.com/pf/money-essentials"));
        resource2Img.setOnClickListener(v -> NavigateTo("https://money.cnn.com/pf/money-essentials"));
        resource2Txt.setOnClickListener(v -> NavigateTo("https://money.cnn.com/pf/money-essentials"));

        m_Resource3.setOnClickListener(v -> NavigateTo("https://finance.yahoo.com/"));
        resource3Img.setOnClickListener(v -> NavigateTo("https://finance.yahoo.com/"));
        resource3Txt.setOnClickListener(v -> NavigateTo("https://finance.yahoo.com/"));
        return m_RootView;
    } /* public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) */


    /**/
    /*
    * NAME
        ResourceFragment::NavigateTo() - Overrides the default onCreateView function for a fragment

    * SYNOPSIS
        void ResourceFragment::NavigateTo(String a_Uri);
        * a_Uri => external link to be directed to

    * DESCRIPTION
        This function will parse the uri that has been passed to it and then, start a new activity to
        direct the user to an external link

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:37pm, 09/04/2021
    */
    /**/
    private void NavigateTo(String a_Uri){
        Uri uri = Uri.parse(a_Uri);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    } /*  private void NavigateTo(String a_Uri) */

    private View m_RootView;
    CardView m_Resource1;
    CardView m_Resource2;
    CardView m_Resource3;

}