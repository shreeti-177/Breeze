//
// Implementation of the OnboardActivity class
//
package com.example.personalfinance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.plaid.client.ApiClient;
import com.plaid.client.model.AccountSubtype;
import com.plaid.client.model.CountryCode;
import com.plaid.client.model.DepositoryFilter;
import com.plaid.client.model.ItemPublicTokenExchangeRequest;
import com.plaid.client.model.ItemPublicTokenExchangeResponse;
import com.plaid.client.model.LinkTokenAccountFilters;
import com.plaid.client.model.LinkTokenCreateRequest;
import com.plaid.client.model.LinkTokenCreateRequestUser;
import com.plaid.client.model.LinkTokenCreateResponse;
import com.plaid.client.model.Products;
import com.plaid.client.request.PlaidApi;
import com.plaid.link.Plaid;
import com.plaid.link.PlaidHandler;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkError;
import com.plaid.link.result.LinkResultHandler;
import com.plaid.link.result.LinkSuccessMetadata;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import retrofit2.Response;

public class OnboardActivity extends AppCompatActivity {

    // constructor to initialize a Plaid Client at the start of the activity
    public OnboardActivity(){
        GetPlaidClient();
    }

    /**/
    /*
    * NAME
        OnboardActivity::onCreate() - Overrides the default onCreate function for a class

    * SYNOPSIS
        void OnboardActivity::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the layout for the onboarding page.
        It will include a carousel that shows tips to the user on the purposes of the app.
        Then, it will set up an onclick listener for the "Link my bank account" button
        to navigate the user to linking their bank account

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:27pm, 02/24/2021
    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        m_CarouselView=findViewById(R.id.carouselView);
        m_CarouselView.setPageCount(m_Images.length);
        m_CarouselView.setImageListener(m_ImageListener);
        Button m_LinkAccount = findViewById(R.id.linkAccountBtn);

        //Configure a temporary token for the plaid clientonce the user click the link button
        m_LinkAccount.setOnClickListener(v -> m_Executor.execute(()->{
            try{
                m_Token = GetPublicToken();
                LinkTokenConfiguration linkTokenConfiguration = new LinkTokenConfiguration.Builder()
                        .token(m_Token)
                        .build();
                PlaidHandler plaidHandler = Plaid.create(getApplication(), linkTokenConfiguration);
                plaidHandler.open(this);
            }
            catch(Exception e) {
                e.getStackTrace();
            }
        }));
    } /*   protected void onCreate(Bundle savedInstanceState) */


    /**/
    /*
    * NAME
        OnboardActivity::GetPublicToken() - Waits for the result and initiates a new activity
        based on the result code

    * SYNOPSIS
        String OnboardActivity::GetPublicToken();

    * DESCRIPTION
        This function will set up a new client to access the Plaid API based on the sandbox configurations.
        Then, it creates a public token to access the API for a first-time use and allows the user to
        authenticate their bank credentials.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:27pm, 02/24/2021
    */
    /**/
    private String GetPublicToken() throws IOException {
        String clientUserId = m_CurrentUser.getUid();
        LinkTokenCreateRequestUser user = new LinkTokenCreateRequestUser().clientUserId(clientUserId);

        DepositoryFilter types = new DepositoryFilter()
                .accountSubtypes(Collections.singletonList(AccountSubtype.CHECKING));

        LinkTokenAccountFilters accountFilters = new LinkTokenAccountFilters()
                .depository(types);

        LinkTokenCreateRequest request= new LinkTokenCreateRequest()
                .user(user)
                .clientName("Personal Finance App")
                .products(Arrays.asList(Products.AUTH, Products.TRANSACTIONS))
                .countryCodes(Arrays.asList(CountryCode.US))
                .language("en")
                .webhook("https://webhook.example.com")
                .linkCustomizationName("default")
                .androidPackageName("com.example.personalfinance")
                .accountFilters(accountFilters);

        Response<LinkTokenCreateResponse> response=client()
                .linkTokenCreate(request)
                .execute();

        assert response.body() != null;
        return response.body().getLinkToken();
    } /*  private String GetPublicToken() */


    // Once the public token is created, launch the link that allows to connect to the bank
    // When user authenticates their bank account, return the account details and transactions upon
    // successful authentication, return error message otherwise
    private final LinkResultHandler m_LinkResultHandler=new LinkResultHandler(
            linkSuccess -> {
                m_PublicToken = linkSuccess.getPublicToken();
                LinkSuccessMetadata metadata= linkSuccess.getMetadata();
                return Unit.INSTANCE;
            },
            linkExit -> {
                LinkError error = linkExit.getError();
                assert error != null;
                return Unit.INSTANCE;
            }
    );

    /**/
    /*
    * NAME
        OnboardActivity::onActivityResult() - Waits for the result and initiates a new activity
        based on the result code

    * SYNOPSIS
        void OnboardActivity::onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
        * requestCode => code to identify from which intent to come back
        * resultCode => code to identify the returned result
        * data => data returned to the activity after completion

    * DESCRIPTION
        This function will make function calls to establish a connection to the Plaid server, get a
        permanent access token and store that access token in the database. When everything is
        completed successfully, it directs the user to the Home page on activity result.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:27pm, 02/24/2021
    */
    /**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!m_LinkResultHandler.onActivityResult(requestCode, resultCode, data)) {
            Log.i(OnboardActivity.class.getSimpleName(), "Not handled by the LinkResultHandler");
        }
        m_Executor.execute(()->{
            try {
                GetAccessToken();
                StoreAccessToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        finishActivity(requestCode);
        startActivity(new Intent(this, HomeActivity.class));
    } /* protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) */



    /**/
    /*
    * NAME
        OnboardActivity::GetAccessToken() - Gets a new safe access token to replace the public token

    * SYNOPSIS
        void OnboardActivity::GetAccessToken();

    * DESCRIPTION
        This function will attempt to contact the Plaid Server with a temporary public token. Once the
        connection is established, it fetches a permanent access token for the client and stores it
        as a string.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:35pm, 02/24/2021
    */
    /**/
    public void GetAccessToken() throws IOException{
        ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken());
        Response<ItemPublicTokenExchangeResponse> response = client()
                .itemPublicTokenExchange(request)
                .execute();
        assert response.body() != null;
        m_AccessToken = response.body().getAccessToken();
    } /*  public void GetAccessToken() */

    /**/
    /*
    * NAME
        OnboardActivity::StoreAccessToken() - Stores the access token in the Firebase database

    * SYNOPSIS
        void OnboardActivity::StoreAccessToken();

    * DESCRIPTION
        This function will attempt to store the access token in the Firebase database for each new user.
        This token can later be accessed when retrieving or updating transactions from the bank.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:35pm, 02/24/2021
    */
    /**/
    private void StoreAccessToken(){

        FirebaseAuth m_Auth = FirebaseAuth.getInstance();
        String a_Uid = m_CurrentUser.getUid();
        DatabaseReference m_BaseDataRef= FirebaseDatabase.getInstance().getReference().child("base-data").child(a_Uid);

        m_BaseDataRef.child("access-token").setValue(m_AccessToken).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "SetAccessToken: success");
                Log.i(TAG, m_AccessToken);
//                Toast.makeText(getApplicationContext(),"Access Token set successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.w(TAG, "SetAccessToken: failure", task.getException());
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } /* private void StoreAccessToken() */



    /**/
    /*
    * NAME
        OnboardActivity::GetPlaidClient() - Sets up a new client for a new user account

    * SYNOPSIS
        void OnboardActivity::GetPlaidClient();

    * DESCRIPTION
        This function will attempt to set up a new client with the encrypted client Id and the secret
        key.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:00pm, 02/26/2021
    */
    /**/
    private void GetPlaidClient(){
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", BuildConfig.PLAID_CLIENT_KEY);
        apiKeys.put("secret", BuildConfig.PLAID_CLIENT_SECRET);
        apiKeys.put("plaidVersion", "2020-09-14");
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(ApiClient.Sandbox);
        m_PlaidClient = apiClient.createService(PlaidApi.class);
    }

    public PlaidApi client(){
        return m_PlaidClient;
    }

    private String publicToken(){
        return m_PublicToken;
    }

    CarouselView m_CarouselView;
    int[] m_Images = {R.drawable.carousel1, R.drawable.carousel2, R.drawable.carousel3};
    ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    private String m_PublicToken;
    protected PlaidApi m_PlaidClient;
    protected String m_AccessToken;
    private String m_Token;
    FirebaseUser m_CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private Map<String, String> m_GeneralCategories;
    private static final String TAG = "OnboardActivity";

    // New instance of an image listener that slides each image to display a new one in the carousel
    ImageListener m_ImageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(m_Images[position]);
            imageView.setAdjustViewBounds(true);
        }
    };

}
