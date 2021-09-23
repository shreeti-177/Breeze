package com.example.personalfinance;


public class User {
//    private String accessToken;
//    private String firstName;
//    private String lastName;
    private String fullName;
    private String email;
    private String Uid;
    private String url;
    public User(){}

    public String GetEmail() {
        return email;
    }

    public void SetEmail(String email) {
        this.email = email;
    }

    public String GetUid() {
        return Uid;
    }

    public void SetUid(String uid) {
        Uid = uid;
    }

//    public User(String accessToken) {
//        this.accessToken = accessToken;
//    }
//    public String getAccessToken() {
//        return accessToken;
//    }
//
//    public void setAccessToken(String accessToken) {
//        this.accessToken = accessToken;
//    }
//
//    public String GetFirstName() {
//        return firstName;
//    }
//
//    public void SetFirstName(String firstName) {
//        this.firstName = firstName;
//    }

//    public String GetLastName() {
//        return lastName;
//    }

//    public void SetLastName(String lastName) {
//        this.lastName = lastName;
//    }

    public String GetUrl() {
        return url;
    }

    public void SetUrl(String url) {
        this.url = url;
    }

    public String GetFullName() {
        return fullName;
    }

    public void SetFullName(String fullName) {
        this.fullName = fullName;
    }
}
