package com.example.personalfinance;

import org.joda.time.DateTime;

public class BaseData {
    private String accessToken;
//    private DateTime dateLinked;
    public BaseData(){}

    public BaseData(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

//    public DateTime getDateLinked() {
//        return dateLinked;
//    }
//
//    public void setDateLinked(DateTime dateLinked) {
//        this.dateLinked = dateLinked;
//    }
}
