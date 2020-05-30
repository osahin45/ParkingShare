package com.example.android.parkingshare.Account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.parkingshare.Login.LoginActivity;
import com.example.android.parkingshare.R;
import com.example.android.parkingshare.Utils.BottomNavigationViewHelper;
import com.example.android.parkingshare.Utils.FirebaseMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 2;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseMethods firebaseMethods;

    private TextView changeNameText,changeSurnameText,changeCarPlaka,editUsername;
    private ImageView changeProfileImage;

    private ListView editList;
    private EditProfileFragment editProfileFragment;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        mContext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: started");
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);

                editList = findViewById(R.id.lvAccountSettings);
                setupSettingList(editList);
                setupBottomNavigationView();


        //backarrow Profile activity e geri dönme
        ImageView imageView = findViewById(R.id.backArrow);
        backArrow(imageView);
    }


    //Bottom navigationn
    private void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
    //geri butonu method
    public void backArrow(ImageView image)
    {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to profile");
                Intent intenToBack = new Intent(mContext,AccountActivity.class);
                startActivity(intenToBack);
                finish();
            }
        });
    }
    //listsettings
    private void setupSettingList(ListView settingsList)
    {
        final ArrayList<String> options = new ArrayList<>();
        options.add("Profil Düzenle"); //fragment 0
        options.add("Çıkış Yap");      //fragment 1
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1,options);
        settingsList.setAdapter(adapter);

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String secilen = options.get(position);
                if (secilen.equals("Profil Düzenle")){
                    FragmentManager fm = getSupportFragmentManager();
                    editProfileFragment = new EditProfileFragment();
                    fm.beginTransaction().replace(R.id.relLayout1,editProfileFragment).commit();
                    bottomNavigationView = findViewById(R.id.navigation_bar);
                    bottomNavigationView.setVisibility(View.INVISIBLE);


                }else if (secilen.equals("Çıkış Yap")){
                    //Logout İşlemi...
                    mAuth.signOut();

                    Intent intentToLogin = new Intent(mContext, LoginActivity.class);
                    startActivity(intentToLogin);
                    finishAffinity();
                }
            }
        });

    }




}

