package com.example.personalfinance;


public class User {
//    private String accessToken;
//    private String firstName;
//    private String lastName;
    private String fullName;
    private String email;
    private String password;
    private String Uid;
//    private String url;
    public User(){}

    public User(String a_fullName, String a_Email, String a_Password){
        this.fullName=a_fullName;
        this.email=a_Email;
        this.password = a_Password;
    }
    public String GetEmail() {
        return email;
    }

    public void SetEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

//    public String GetUrl() {
//        return url;
//    }
//
//    public void SetUrl(String url) {
//        this.url = url;
//    }

    public String GetFullName() {
        return fullName;
    }

    public void SetFullName(String fullName) {
        this.fullName = fullName;
    }
}
