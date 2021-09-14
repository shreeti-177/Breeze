package com.example.personalfinance;

import android.util.Log;

import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;

import java.util.HashMap;

public class PlaidClient {
    public PlaidClient(){
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", BuildConfig.PLAID_CLIENT_KEY);
        apiKeys.put("secret", BuildConfig.PLAID_CLIENT_SECRET);
        apiKeys.put("plaidVersion", "2020-09-14");
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(ApiClient.Sandbox);
        m_PlaidClient = apiClient.createService(PlaidApi.class);
    }

    public PlaidApi client(){
        Log.i("Client","Here");
        Log.i("Client",String.valueOf(m_PlaidClient));
        return m_PlaidClient;
    }
    private static PlaidApi m_PlaidClient;
}