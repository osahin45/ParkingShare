package com.example.android.parkingshare.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.parkingshare.R;
import com.example.android.parkingshare.Utils.DialogHelper;
import com.example.android.parkingshare.Utils.FirebaseMethods;
import com.example.android.parkingshare.Utils.StringManipulation;
import com.example.android.parkingshare.Utils.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.internal.AbstractReadableBuffer;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore ;
    private CollectionReference colRef;
    private FirebaseMethods firebaseMethods;
    //Firebase
    private List<String> usernames;

    private Users user;
    private EditText emailText;
    private EditText passwordText;
    private EditText userIdText;
    private String email, password, username;
    private Context mContext;
    private ProgressBar mProgressBar;
    private String userID;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initWidgets();
        user = new Users();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);
        mFireStore = FirebaseFirestore.getInstance();
        usernames = new ArrayList<>();
        usernameKontrol();

    }
    private void initWidgets(){
        emailText = findViewById(R.id.emailText_register);
        passwordText = findViewById(R.id.password_register);
        userIdText = findViewById(R.id.register_username);
        mContext = RegisterActivity.this;
        mProgressBar = findViewById(R.id.progressbar_register);
        mProgressBar.setVisibility(View.GONE);
    }

    public void registerKayitOl(View view) {

        email = emailText.getText().toString();
        password = passwordText.getText().toString();
        username = userIdText.getText().toString();

        user.setEmail(email);
        user.setUsername(username);

        if (email.matches("")) {
            AlertDialog.Builder builder = DialogHelper.alertBuilder(mContext);
            builder.setTitle("Parking Share");
            builder.setMessage("Email girmediniz!");
            builder.show();
        }else if (username.matches("")){
            AlertDialog.Builder builder = DialogHelper.alertBuilder(mContext);
            builder.setTitle("Parking Share");
            builder.setMessage("User ID girmediniz!");
            builder.show();

        }else if (password.matches("")){
            AlertDialog.Builder builder = DialogHelper.alertBuilder(mContext);
            builder.setTitle("Parking Share");
            builder.setMessage("Password girmediniz!");
            builder.show();
        }else if (usernames.contains(StringManipulation.expandUsername(username))){
            AlertDialog.Builder builder = DialogHelper.alertBuilder(mContext);
            builder.setTitle("Parking Share");
            builder.setMessage("Kullanıcı Adı Alınmış!");
            builder.show();
        }
        else{
            mProgressBar.setVisibility(View.VISIBLE);

            registerFirebase();
            mProgressBar.setVisibility(View.GONE);

        }
    }

    private void registerFirebase(){
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(mContext,"Kullanıcı Oluştu Giriş Yapılıyor",Toast.LENGTH_LONG).show();
                userID = mAuth.getCurrentUser().getUid();

                firebaseMethods.addNewUser("","",email,"",username,userID);

                Intent intentToLogin = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intentToLogin);
                finishAffinity();
                mProgressBar.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void usernameKontrol()
    {
        mFireStore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.w(TAG, "onEvent: Listen Failed ",e);
                    return;
                }
                for (DocumentSnapshot documentSnapshot:querySnapshot.getDocuments()){
                    Map<String,Object> data = documentSnapshot.getData();
                    String userName = (String) data.get("username");
                    usernames.add(userName);
                }
            }
        });


    }


}

