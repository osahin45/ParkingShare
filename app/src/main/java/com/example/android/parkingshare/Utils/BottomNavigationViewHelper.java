package com.example.android.parkingshare.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android.parkingshare.Account.AccountActivity;
import com.example.android.parkingshare.Home.HomeActivity;
import com.example.android.parkingshare.Maps.MapsActivity;
import com.example.android.parkingshare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper  {
    private static final String TAG = "BottomNavigationViewHel";


    public static void enableNavigation(final Context context, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.ic_home:
                        Intent intent1 = new Intent(context, HomeActivity.class); //activity_num 0
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_map:
                        Intent intent2 = new Intent(context, MapsActivity.class); // activity_num 1
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_account:
                        Intent intent3 = new Intent(context, AccountActivity.class);//activity_num 2
                        context.startActivity(intent3);
                        break;

                }
                return false;
            }
        });
    }


}
