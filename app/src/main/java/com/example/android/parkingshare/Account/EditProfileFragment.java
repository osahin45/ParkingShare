package com.example.android.parkingshare.Account;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.parkingshare.Login.LoginActivity;
import com.example.android.parkingshare.R;
import com.example.android.parkingshare.Utils.BottomNavigationViewHelper;
import com.example.android.parkingshare.Utils.FirebaseMethods;
import com.example.android.parkingshare.Utils.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    private Bitmap selectedImage;
    private TextView changePhoto;
    private ImageView changeProfileImage,saveChangeImage;
    private TextView changeNameText,changeSurnameText,changeCarPlaka,editUsername;
    private ProgressBar progressBar;

    private BottomNavigationView bottomNavigationView;
    //Firebase
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private Uri imageData;
    private FirebaseMethods firebaseMethods;
    private FirebaseFirestore mFireStore ;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Log.d(TAG, "onCreateView: starting edit profile fragment");

        //Firebase init.
        mFireStore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(getActivity());


        //Backarrow init.
        ImageView backArrowImage = view.findViewById(R.id.backArrow);
        backArrow(backArrowImage);
        //init widget
        //Fotografın gösterildiği image.
        changeProfileImage = view.findViewById(R.id.profile_image_change);
        changePhoto = view.findViewById(R.id.changeProfilePhoto);

        changeNameText = view.findViewById(R.id.changeNameText);
        changeSurnameText = view.findViewById(R.id.changeSurnameText);
        changeCarPlaka = view.findViewById(R.id.changeCarPlaka);
        editUsername = view.findViewById(R.id.edit_fragment_userid);

        progressBar = view.findViewById(R.id.edit_profile_progressbar);

        //YeşilTikbuton
        saveChangeImage = view.findViewById(R.id.savaChangesImage);

        userID = mAuth.getCurrentUser().getUid();
        firebaseMethods.getDataFireStore(userID,changeNameText,changeSurnameText,changeCarPlaka,changeProfileImage,editUsername);

        //Fotograf seçtirmek için çalışan click
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChangeProfileImage(v);


            }
        });


        //Girilen degerleri ve Fotografı cloud a gönderen button
        saveChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoSaveStorage();

                saveUserInfo();

               // Intent intentToLogin = new Intent(getActivity(),AccountSettingsActivity.class);
               //startActivity(intentToLogin);

            }
        });

        progressBar.setVisibility(View.GONE);
        return view;
    }

    //Profil Fotosu seçtirme
    public void setChangeProfileImage(View view) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }else{
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }

        //izin kontrol
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        
        if (requestCode==1)
        {
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }
        
        
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //Dönen sonuc
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==2 && resultCode == RESULT_OK && data !=null){
            imageData =   data.getData();
            try {
                if (Build.VERSION.SDK_INT >=28){
                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(),imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    changeProfileImage.setImageBitmap(selectedImage);

                }else{
                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageData);
                    changeProfileImage.setImageBitmap(selectedImage);

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    //Geri butonu
    public void backArrow(ImageView image)
    {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to profile");
                Intent intentToSettings = new Intent(getActivity(),AccountSettingsActivity.class);
                startActivity(intentToSettings);
                requireActivity().finish();
            }
        });
    }
    //Fotografı Storage a kaydetme
    private void photoSaveStorage(){
        if (imageData !=null){
            storageReference.child("ProfilePhoto").child(userID).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(),"Profil Fotoğrafı Kaydedildi",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),"Eklenemedi",Toast.LENGTH_LONG).show();
                }
            });
        }



    }
    //Kullanıcı Bilgilerini Kaydetme
    private void saveUserInfo(){

        final String userID = mAuth.getCurrentUser().getUid();
        final Users user = new Users();


        StorageReference newReference = FirebaseStorage.getInstance().getReference("ProfilePhoto").child(userID);
         //user.setPhotoUrl(newReference.getDownloadUrl().toString());

        user.setAd(changeNameText.getText().toString());
        user.setSoyad(changeSurnameText.getText().toString());
        user.setPlaka(changeCarPlaka.getText().toString());

            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    final String userID = mAuth.getCurrentUser().getUid();
                    String downloadUrl = uri.toString();
                    mFireStore.collection("Users").document(userID).update("photo",downloadUrl);
                    //Değiştirilen Kısım
                    mFireStore.collection("Users").document(userID).update("name",user.getAd());
                    mFireStore.collection("Users").document(userID).update("surname",user.getSoyad());
                    mFireStore.collection("Users").document(userID).update("plaka",user.getPlaka());
                    Toast.makeText(getActivity(),"Kullanıcı Bilgileri Kaydedildi",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),"Fotograf Cloud Firestore da Update Edilemedi.",Toast.LENGTH_LONG).show();
                }
            });
    }

}
