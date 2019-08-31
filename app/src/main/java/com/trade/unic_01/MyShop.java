package com.trade.unic_01;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trade.unic_01.dataclasses.SubscribedShopsClassJava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.Objects;

public class MyShop extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myadapter;
    private RecyclerView.LayoutManager layoutManager;
   // private FirebaseFirestore mDatabase;
    private ArrayList<ShopQuickDetails> MyShops;
   // private ArrayList<String> MyShopID;

    //new code by akshat
    // recommended to use subscribedShopsAdapter in recycler view
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // private ArrayList<ShopQuickDetails> shopQuickDetails=new ArrayList<>();
    private ArrayList<SubscribedShopsClassJava> subscribedShopsList=new ArrayList<>();
    private Query query;
    private ListenerRegistration registration;
    private FloatingActionButton addShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shop);
        recyclerView = findViewById(R.id.shop_view);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
      /*  MyShops=new ArrayList<>();
        MyShopID=new ArrayList<>();*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myadapter=new MyShopAdapter(this,subscribedShopsList);
        recyclerView.setAdapter(myadapter);


       addShop=findViewById(R.id.fabAddShop);
        addShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyShop.this,AddShopKt.class));
            }
        });

        getFirestoreData();
    }


    private void getFirestoreData() {
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        query = db.collection("users").document(Objects.requireNonNull(mAuth.getUid())).collection("shopscreated");

        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Subscribed2", "Listen failed.", e);
                    return;
                }


                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("Subscribed", "New  " + dc.getDocument().getData());
                            /*for (int i = 0; i < subscribedShopsList.size(); i++) {
                                if (!subscribedShopsList.get(i).getShopid().equals(dc.getDocument().get("shopid"))) {
                                    subscribedShopsList.add(dc.getDocument().toObject(SubscribedShopsClassJava.class));

                                }
                            }*/
                            subscribedShopsList.add(dc.getDocument().toObject(SubscribedShopsClassJava.class));

                            break;
                        case MODIFIED:
                            Log.d("Subscribed", "Modified  " + dc.getDocument().getData());
                           /* for (int i = 0; i < productList.size(); i++) {
                                if (productList.get(i).getItem_code().equals(dc.getDocument().get("item_code"))) {
                                    productList.add(i, dc.getDocument().toObject(ProductDataClass.class));
                                }
                            }*/
                            break;
                        case REMOVED:
                            for (int i = 0; i < subscribedShopsList.size(); i++) {
                                if (subscribedShopsList.get(i).getShopid().equals(dc.getDocument().get("shopid"))) {
                                    subscribedShopsList.remove(i);
                                }
                            }
                            Log.d("Subscribed", "Removed  " + dc.getDocument().getData());
                            break;
                    }
                }
                // call function to supply data
                upadateRecyclerView(subscribedShopsList);
                Log.d("Subscribed2", "data "+subscribedShopsList.toString());

            }
        });

    }

    private void upadateRecyclerView(ArrayList<SubscribedShopsClassJava> subscribedList ){

        myadapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        registration.remove();
        super.onDestroy();
    }
}
