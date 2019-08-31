package com.trade.unic_01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.trade.unic_01.dataclasses.ShopDetails;
import com.trade.unic_01.dataclasses.ShopDetailsJava;
import com.trade.unic_01.dataclasses.SubscribedShopsClassJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class    ShopProfile extends AppCompatActivity {
    int coverpic;
    int status;
    LinearLayout Shop;
    private String shopid;

    private ShopDetailsJava shopDetails;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Query query;
    private ListenerRegistration registration;
    private ProgressBar buttonPress;
    private TextView stat4;
    private ImageView ivProfileCoverPhoto,ivProfilePic;
    LinearLayout subsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile);
        ivProfileCoverPhoto=findViewById(R.id.ivShopCoverPhoto);
        ivProfilePic=findViewById(R.id.imageView6);
        buttonPress = findViewById(R.id.prog_bar_shop_profile);
        coverpic=getIntent().getIntExtra("pic",0);
        status = getIntent().getIntExtra("status",1);
        Shop=findViewById(R.id.Shop);
        shopid = getIntent().getStringExtra("shopid");
        Window window = this.getWindow();
        stat4 = findViewById(R.id.textView5);
        if(status==0) stat4.setText("Add product");
        else stat4.setText("Subscribe");
        db  = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        HashMap<String ,String > dataMap = new HashMap<>();
        db.collection("shops").document(shopid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                        String shopname = document.getString("shopname");
                        String shopimage = document.get("imagelink").toString();
                    dataMap.put("shopid",shopid);
                    dataMap.put("shopname",shopname);
                    dataMap.put("imagelink",shopimage);
                    Glide
                            .with(ShopProfile.this)
                            .load(shopimage)
                            .into(ivProfileCoverPhoto);

                }
                else{
                    Log.d("ShopProfile78", "Error getting documents:shopid "+shopid+"  "+ task.getException());
                    //Toast.makeText(this,"error getting shop info",Toast.LENGTH_LONG).show();
                }
            }
        });


        getFirestoreData();
        Shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ShopProfile.this,PersonalizedShops.class);
                i.putExtra("shopid",shopid);
                //send shopkeepers photo
                startActivity(i);
                finish();

            }
        });

        subsButton = findViewById(R.id.llsubscribe);
        subsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPress.setVisibility(View.VISIBLE);
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Snackbar.make(view, "Subscribing", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                DocumentReference docRef = db.collection("users").document(mAuth.getUid()).collection("subscribed").document(shopid);
                if(status==1) {
                    docRef.set(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                buttonPress.setVisibility(View.GONE);
                                Toast.makeText(ShopProfile.this, "Successfully Subscribed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShopProfile.this, "Please try again later", Toast.LENGTH_SHORT).show();
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                buttonPress.setVisibility(View.GONE);
                            }
                        }

                    });
                }
                else startActivity(new Intent(ShopProfile.this,AddProductKt.class));
            }
        });

    }

    private void getFirestoreData() {

        DocumentReference docRef = db.collection("shops").document(shopid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("ShopProfile", "DocumentSnapshot data: " + document.getData());
                        shopDetails=document.toObject(ShopDetailsJava.class);
                        dataRecievedFromFirestore(shopDetails);

                    } else {
                        Log.d("ShopProfile1", "No such document");
                    }
                } else {
                    Log.d("ShopProfile3", "get failed with ", task.getException());
                }
            }
        });


    }

    private void dataRecievedFromFirestore(ShopDetailsJava shopDetails){
        //TODO: set the data recived from firestore
    }
}
