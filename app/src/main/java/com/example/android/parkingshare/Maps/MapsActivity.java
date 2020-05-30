package com.example.android.parkingshare.Maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.parkingshare.Account.AccountActivity;
import com.example.android.parkingshare.Home.HomeActivity;
import com.example.android.parkingshare.R;
import com.example.android.parkingshare.Utils.BottomNavigationViewHelper;
import com.example.android.parkingshare.Utils.DialogHelper;
import com.example.android.parkingshare.Utils.ParkingLocation;
import com.example.android.parkingshare.Utils.RecyclerAdapter;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener

 {
     private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Context mContext = MapsActivity.this;
    private static final int ACTIVITY_NUM = 1;

    //widget
     private EditText mSearchText;
    //Location
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private LatLng userLocation;
    //Firebase İşlemleri.
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private BitmapDescriptor smallMarkerIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Log.d(TAG, "onCreate: starting");

        mSearchText = findViewById(R.id.search_edit_text);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setupBottomNavigationView();

        Intent intentToHome = getIntent();
        String  accountMesaj = intentToHome.getStringExtra(AccountActivity.ACCOUNT_MESSAGE);
        String homeMesaj = intentToHome.getStringExtra(HomeActivity.HOME_MESSAGE);
        if (accountMesaj !=null){
            mSearchText.setText(accountMesaj);
        }else if (homeMesaj !=null){
            mSearchText.setText(homeMesaj);
        }


        init();
    }
    //Account Listview Click item gelen adres method.

    //Edittex ayarları ve init.
    private  void init(){
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction()==KeyEvent.ACTION_DOWN
                || event.getAction()==KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
        HideSoftKeyboard();
    }

    //Edit text e girilen adres bilgilerinin haritada bulna method.
    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size()>0){
            Address address = list.get(0);
           // Log.d(TAG, "geoLocate: Found a location"+address.toString());
           moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }
    //Edit text girilen adrese camera hareket method.
    private void moveCamera(LatLng latlng,float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));
        if (!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title).icon(smallMarkerIcon);
            mMap.addMarker(options);
        }
        HideSoftKeyboard();
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
     //Marker size setting
    private void setSmallMarkerIcon() {
        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.parkingmarker);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Marker boyut ayarlandıgı method.
        setSmallMarkerIcon();
        //Kullanıcı lokasyon alındıgı method.
        getUserLocation();
        //İzinlerin alındığı method.

        mMap.setOnMapLongClickListener(this);
        //Db de kayıtlı yerleri getiren methodş.
        getLocations();

        //Account Listview da tıklanan item ın adres bilgisinin geldiği method.
        //getAccountIntent();
        getLocationpermission();
        uiSettings();
        init();


    }
    private  void uiSettings(){

        UiSettings uis = mMap.getUiSettings();
        uis.setCompassEnabled(true);
        uis.setRotateGesturesEnabled(true);
        uis.setMapToolbarEnabled(true);
        uis.setMyLocationButtonEnabled(true);
        //Telefon haritasını acıp directions oluşturur
        MapView mapView = new MapView(mContext);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setMapToolbarEnabled(false);
            }
        });

    }
    //Firestore da kayıtlı park yerlerini getirir
    private void getLocations(){

             firebaseFirestore.collection("Parking Location").orderBy("geopoint").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                             if (userLocation!=null){
                                 Location.distanceBetween(userLocation.latitude,userLocation.longitude,getPoint.latitude,getPoint.longitude,results);
                                 mMap.addMarker(new MarkerOptions().position(getPoint).title("Parking Point").icon(smallMarkerIcon)
                                         .snippet("Distance: "+ new DecimalFormat("#.##").format(results[0]/1000)+" KM"));
                             }


                         }
                     }
                 }
             });

    }

    private void getUserLocation(){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //Toast.makeText(mContext,location.toString(),Toast.LENGTH_LONG).show();
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                @Override
                public void onProviderEnabled(String provider) {
                }
                @Override
                public void onProviderDisabled(String provider) {
                }
            };
    }

    //izinler
    private  void getLocationpermission()
    {
            if (Build.VERSION.SDK_INT >=23){
                if (ContextCompat.checkSelfPermission(mContext,permission[0])!=PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,permission[1])!=PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,500,locationListener);
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation!=null){
                        userLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        mMap.setMyLocationEnabled(true);

                    }

                }

            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,500,locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation!=null){
                    userLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                    mMap.setMyLocationEnabled(true);
                }


            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length>0){
            if (requestCode==1){
                if (ContextCompat.checkSelfPermission(this,permission[0])==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,permission[1])==PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,500,locationListener);
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation!=null){
                            userLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                            mMap.setMyLocationEnabled(true);
                        }

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    private void HideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

 }
