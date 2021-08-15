package com.example.personalfinance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Util {
    public static Months getMonth() {
        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        DateTime a_Now = new DateTime();
        Months a_Month = Months.monthsBetween(a_Epoch, a_Now);
        return a_Month;
    }

    public static PlaidApi PlaidClient(){
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", BuildConfig.PLAID_CLIENT_KEY);
        apiKeys.put("secret", BuildConfig.PLAID_CLIENT_SECRET);
        apiKeys.put("plaidVersion", "2020-09-14");
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(ApiClient.Sandbox);
        return apiClient.createService(PlaidApi.class);
    }

    public static List<String> GetExistingCategories() {
        List<String> basicCategories = new ArrayList<String>(){{
            add("Apparel");
            add("Community");
            add("Food");
            add("Education");
            add("Healthcare");
            add("Merchandise");
            add("Miscellaneous");
            add("Payments");
            add("Recreation");
            add("Travel");
        }};

        return basicCategories;
    }


}
