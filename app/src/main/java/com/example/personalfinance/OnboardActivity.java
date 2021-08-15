package com.example.personalfinance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.plaid.client.ApiClient;
import com.plaid.client.model.AccountSubtype;
import com.plaid.client.model.CategoriesGetResponse;
import com.plaid.client.model.Category;
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
import com.plaid.link.result.LinkAccount;
import com.plaid.link.result.LinkAccountSubtype;
import com.plaid.link.result.LinkError;
import com.plaid.link.result.LinkErrorCode;
import com.plaid.link.result.LinkExitMetadata;
import com.plaid.link.result.LinkResultHandler;
import com.plaid.link.result.LinkSuccessMetadata;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import retrofit2.Response;

public class OnboardActivity extends AppCompatActivity {
    private final LinkResultHandler m_LinkResultHandler=new LinkResultHandler(
            linkSuccess -> {
                m_PublicToken = linkSuccess.getPublicToken();
                LinkSuccessMetadata metadata= linkSuccess.getMetadata();

                for (LinkAccount account: metadata.getAccounts()) {
                    String accountId = account.getId();
                    String accountName = account.getName();
                    String accountMask = account.getMask();
                    LinkAccountSubtype accountSubtype = account.getSubtype();
                }
                String institutionId = metadata.getInstitution().getId();
                String institutionName = metadata.getInstitution().getName();

                return Unit.INSTANCE;
            },
            linkExit -> {
                LinkError error = linkExit.getError();
                LinkErrorCode errorCode=error.getErrorCode();
                String errorMessage = error.getErrorMessage();
                String displayMessage = error.getDisplayMessage();

                LinkExitMetadata metadata = linkExit.getMetadata();
                String institutionId = metadata.getInstitution().getId();
                String institutionName = metadata.getInstitution().getName();
                String linkSessionId = metadata.getLinkSessionId();
                String requestId = metadata.getRequestId();
                return Unit.INSTANCE;
            }
    );

    public OnboardActivity(){
        GetPlaidClient();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        Button m_LinkAccount = findViewById(R.id.linkAccountBtn);
        m_LinkAccount.setOnClickListener(v -> {
            m_Executor.execute(()->{
                try{
                    m_Token = GetNewToken();
                    LinkTokenConfiguration linkTokenConfiguration = new LinkTokenConfiguration.Builder()
                            .token(m_Token)
                            .build();
                    PlaidHandler plaidHandler = Plaid.create(getApplication(), linkTokenConfiguration);
                    plaidHandler.open(this);
                }
                catch(Exception e) {
                    e.getStackTrace();
                }
            });
        });
    }

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
                GetAllCategories();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        finishActivity(requestCode);
        startActivity(new Intent(this, HomePageActivity.class));
    }

    private void GetAllCategories() throws IOException {
        Response<CategoriesGetResponse> response = client().categoriesGet(new Object()).execute();
        assert response.body() != null;
        List<Category> allCategories = response.body().getCategories();
        Log.i("Categories",String.valueOf(allCategories));
        m_GeneralCategories = new HashMap<>();
        for (Category c:allCategories) {
            if (c.getHierarchy().size() == 1) {
                String general = c.getHierarchy().get(0);
                if (!m_GeneralCategories.containsValue(general)) {
                    m_GeneralCategories.put(c.getCategoryId(), general);
                }
            }
        }
        Log.i(TAG, String.valueOf(m_GeneralCategories));
        Log.i(TAG, "===============================");
        m_CloudStore.collection("categories").document("general").set(m_GeneralCategories)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    private void StoreAccessToken(){

        FirebaseAuth m_Auth = FirebaseAuth.getInstance();
        String a_Uid = m_CurrentUser.getUid();
        DatabaseReference m_BaseDataRef= FirebaseDatabase.getInstance().getReference().child("base-data").child(a_Uid);

        DateTime linkedDate = DateTime.now();

        BaseData baseData = new BaseData(m_AccessToken);

        m_BaseDataRef.setValue(baseData).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "SetAccessToken: success");
                Log.i(TAG, m_AccessToken);
                Toast.makeText(getApplicationContext(),"Access Token set successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.w(TAG, "SetAccessToken: failure", task.getException());
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetAccessToken() throws IOException{
        ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken());
        Response<ItemPublicTokenExchangeResponse> response = client()
                .itemPublicTokenExchange(request)
                .execute();
        m_AccessToken = response.body().getAccessToken();
    }

    private String GetNewToken() throws IOException {
//        GetPlaidClient();
        String clientUserId = m_CurrentUser.getUid();
        LinkTokenCreateRequestUser user = new LinkTokenCreateRequestUser().clientUserId(clientUserId);

        DepositoryFilter types = new DepositoryFilter()
                .accountSubtypes(Arrays.asList(AccountSubtype.CHECKING));

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
                .androidPackageName("com.example.personalfinanceapp")
                .accountFilters(accountFilters);

        Response<LinkTokenCreateResponse> response=client()
                .linkTokenCreate(request)
                .execute();

        assert response.body() != null;
        return response.body().getLinkToken();
    }

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

    ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    private String m_PublicToken;
    protected PlaidApi m_PlaidClient;
    private String m_AccessToken;
    private String m_Token;
    FirebaseUser m_CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private Map<String, String> m_GeneralCategories;
    FirebaseFirestore m_CloudStore = FirebaseFirestore.getInstance();
    private static final String TAG = "OnboardActivity";

}
