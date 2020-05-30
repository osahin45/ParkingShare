package com.example.android.parkingshare.Utils;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore ;
    private DocumentReference docRef;
    private Context mContext;
    private Users user;


    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        mContext = context;

    }

    public void addNewUser(String ad,String soyad,String email,String plaka,String username,String userID){
     user = new Users(ad,soyad,email,plaka,StringManipulation.expandUsername(username));


           HashMap<String,Object> userMap = new HashMap<>();
           userMap.put("name",user.getAd());
           userMap.put("surname",user.getSoyad());
           userMap.put("email",user.getEmail());
           userMap.put("plaka",user.getPlaka());
           userMap.put("username",user.getUsername());
           userMap.put("photo",user.getPhotoUrl());

           mFireStore.collection("Users").document(userID).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                   Log.d(TAG, "onSuccess: Firestore data eklendi");

               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Log.d(TAG, "onFailure: firestore data eklenemedi");
               }
           });

       }

       public void getDataFireStore(final String userID, final TextView accountName, final TextView surName, final TextView carPlaka, final ImageView profilePhoto, final TextView username){


            mFireStore.collection("Users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e !=null){
                                Toast.makeText(mContext,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                            }
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()){

                        Map<String,Object>data = documentSnapshot.getData();
                        String accName =(String) data.get("name");
                        String accSurname =(String) data.get("surname");
                        String accPlaka =(String) data.get("plaka");
                        String accPhotoUrl =(String) data.get("photo");
                        String accUserName = (String) data.get("username");
                        accountName.setText(accName);
                        surName.setText(accSurname);
                        carPlaka.setText(accPlaka);
                        username.setText(accUserName);
                        Picasso.get().load(accPhotoUrl).into(profilePhoto);
                    }
                    Toast.makeText(mContext,"Veriler Geliyoor",Toast.LENGTH_LONG).show();
                }
            });



       }


}


