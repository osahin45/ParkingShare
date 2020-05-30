package com.example.android.parkingshare.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.parkingshare.Login.LoginActivity;
import com.example.android.parkingshare.Maps.MapsActivity;
import com.example.android.parkingshare.R;
import com.example.android.parkingshare.Utils.BottomNavigationViewHelper;
import com.example.android.parkingshare.Utils.RecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements RecyclerAdapter.OnAdresListener {
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    public  static final String HOME_MESSAGE = "homeadres";
    private Context mContext = HomeActivity.this;
    public static SQLiteDatabase database;

    private GoogleMap mMap;
    //Firebase
    private FirebaseAuth mAuth;
    private LatLng userLocation;
    private LocationManager locationManager;
    RecyclerAdapter recyclerAdapter;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String> addressList;
    private ArrayList<String>distanceList;
    //Location


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG,"onCreate:starting");
        mAuth = FirebaseAuth.getInstance();


        progressBar = findViewById(R.id.home_progress);
        if (mAuth.getCurrentUser()==null)
        {
            Intent intentToLogin = new Intent(mContext, LoginActivity.class);
            startActivity(intentToLogin);
            finish();
            Toast.makeText(mContext, "Lütfen Giriş Yapınız!", Toast.LENGTH_SHORT).show();
        }

        addressList = new ArrayList<>();
        distanceList = new ArrayList<>();


        firebaseFirestore = FirebaseFirestore.getInstance();
        setupBottomNavigationView();
        locationManager  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        assert provider != null;
        Location myLocation = locationManager.getLastKnownLocation(provider);
        assert myLocation != null;
        userLocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

        getLocations();

        //Recycler view
        recyclerAdapter = new RecyclerAdapter(addressList,distanceList,this);
        recyclerView = findViewById(R.id.home_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(recyclerAdapter);


    }
    private void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView =(BottomNavigationView) findViewById(R.id.navigation_bar);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }



    private void getLocations(){

        firebaseFirestore.collection("Parking Location").orderBy("geopoint", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e !=null){
                    Log.d(TAG, "onEvent: "+e.getLocalizedMessage().toString());
                }
                if (querySnapshot !=null){

                    for (QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                        GeoPoint geoPoint = queryDocumentSnapshot.getGeoPoint("geopoint");
                        assert geoPoint != null;
                        LatLng getPoint = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
                        float[] results = new float[1];
                        List<Address> adres;
                        Location.distanceBetween(userLocation.latitude,userLocation.longitude,getPoint.latitude,getPoint.longitude,results);
                        String tamAdres = " ";
                        Geocoder coder = new Geocoder(mContext, Locale.getDefault());
                        try {
                            adres =  coder.getFromLocation(getPoint.latitude,getPoint.longitude,1);
                            if (adres.get(0).getAddressLine(0)!=null){
                                tamAdres +=adres.get(0).getAddressLine(0);

                            }else{
                                tamAdres = "New Place";
                            }
                            addressList.add(tamAdres);
                            distanceList.add(String.valueOf(results[0]));

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        recyclerAdapter.notifyDataSetChanged();

                    }
                }
            }
        });

    }


    @Override
    public void OnAdresClick(int position) {
        Geocoder coder = new Geocoder(HomeActivity.this);
        Intent intent = new Intent(mContext, MapsActivity.class);
        intent.putExtra(HOME_MESSAGE,addressList.get(position));
        startActivity(intent);

        try {
            database = this.openOrCreateDatabase("Adres",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS adres(adres VARCHAR)");
            String toCompile = "INSERT INTO adres (adres) VALUES (?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);
            sqLiteStatement.bindString(1,addressList.get(position));

            sqLiteStatement.execute();
        }catch (Exception e){

        }

        progressBar.setVisibility(View.GONE);
    }

}


