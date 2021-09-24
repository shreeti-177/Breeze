//
// Implementation of the Main Activity class
// This is the starting point of the application
//
package com.example.personalfinance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /**/
    /*
    * NAME
        MainActivity::onCreate() - Overrides the default onCreate function for a class

    * SYNOPSIS
        void MainActivity::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will be the first one to be called once the app is launched.
        It will then attempt to call the login page, which is eventually what the user sees once the
        app is launched.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:27am, 02/04/2021
    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }/* void MainActivity::onCreate(Bundle savedInstanceState); */
}