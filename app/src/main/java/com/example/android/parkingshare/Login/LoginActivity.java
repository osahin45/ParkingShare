package com.example.android.parkingshare.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.parkingshare.Home.HomeActivity;
import com.example.android.parkingshare.Maps.MapsActivity;
import com.example.android.parkingshare.R;
import com.example.android.parkingshare.Utils.DialogHelper;
import com.example.android.parkingshare.Utils.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private Users user;
    private EditText emailText;
    private EditText passwordText;
    private String email, password;
    private Context mContext;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: starting ");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        initWidgets();

        if (firebaseUser !=null){
            Intent intentToHome = new Intent(mContext,HomeActivity.class);
            startActivity(intentToHome);
            finish();
        }
    }

    public void signIn(final View view) {
            email = emailText.getText().toString();
            password = passwordText.getText().toString();

            if (email.matches("")){
                AlertDialog.Builder builder = DialogHelper.alertBuilder(mContext);
                builder.setTitle("Parking Share");
                builder.setMessage("Email girmediniz!");
                builder.show();
            }else if (password.matches("")){
                AlertDialog.Builder builder = DialogHelper.alertBuilder(mContext);
                builder.setTitle("Parking Share");
                builder.setMessage("Password girmediniz!");
                builder.show();
            }else{
                mProgressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intentToHome = new Intent(mContext,HomeActivity.class);
                        startActivity(intentToHome);
                        mProgressBar.setVisibility(View.GONE);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
    }

    public void setRegisterActivity(View view) {
        Intent intentToRegister = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intentToRegister);
    }
    private void initWidgets(){
        emailText = findViewById(R.id.emailText_login);
        passwordText = findViewById(R.id.password_login);
        mContext = LoginActivity.this;
        mProgressBar = findViewById(R.id.progressbar_login);
        mProgressBar.setVisibility(View.GONE);
    }
}
