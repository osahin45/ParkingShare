package com.example.android.parkingshare.Account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.android.parkingshare.Home.HomeActivity;
import com.example.android.parkingshare.Login.LoginActivity;
import com.example.android.parkingshare.Maps.MapsActivity;
import com.example.android.parkingshare.R;
import com.example.android.parkingshare.Utils.BottomNavigationViewHelper;
import com.example.android.parkingshare.Utils.FirebaseMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private Context mContext = AccountActivity.this;
    private static final int ACTIVITY_NUM = 2;
    public static final String ACCOUNT_MESSAGE = "accountadres";
    private ProgressBar mProgressBar;
    TextView accountName,accountSurname,accountPlaka,accountUserId;
    ImageView accountPhoto;

    //List
    static ArrayList<String> accountAdres = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    //Edit Fragment

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseMethods firebaseMethods;
    private ListView listView;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Log.d(TAG, "onCreate: starting");
        initWidget();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        String userID = mAuth.getCurrentUser().getUid();
        firebaseMethods = new FirebaseMethods(mContext);

        firebaseMethods.getDataFireStore(userID,accountName,accountSurname,accountPlaka,accountPhoto,accountUserId);

        if (user ==null){
            Intent intentToLogin = new Intent(mContext, LoginActivity.class);
            intentToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentToLogin);
        }

        listView = findViewById(R.id.account_listview);

        try {
            HomeActivity.database = this.openOrCreateDatabase("Adres",MODE_PRIVATE,null);
            Cursor cursor = HomeActivity.database.rawQuery("SELECT  * FROM adres",null);
            int adresIx = cursor.getColumnIndex("adres");

            while (cursor.moveToNext()){
                String adresString = cursor.getString(adresIx);
                if (!accountAdres.contains(adresString)){
                    accountAdres.add(adresString);
                }
            }
            cursor.close();
        }catch (Exception e){

        }

            arrayAdapter = new ArrayAdapter<>(mContext,R.layout.snippet_listview,R.id.account_address_text,accountAdres);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intentToMap = new Intent(mContext,MapsActivity.class);
                    intentToMap.putExtra(ACCOUNT_MESSAGE,accountAdres.get(position));
                    startActivity(intentToMap);
                }
            });

        setupBottomNavigationView();
        setupToolbar();


    }
    private void initWidget(){
        accountName = findViewById(R.id.user_name);
        accountSurname = findViewById(R.id.user_surname);
        accountPlaka = findViewById(R.id.arac_plaka);
        accountUserId = findViewById(R.id.user_id);
        accountPhoto = findViewById(R.id.profile_photo);

    }
    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView) findViewById(R.id.profile_Menu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent intent = new Intent(mContext,AccountSettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
