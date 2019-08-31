package com.trade.unic_01;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.trade.unic_01.dataclasses.SubscribedShopsClassJava;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearMe extends Fragment {
    private RecyclerView recyclerView;

    private RecyclerView.Adapter myadapter;
    private RecyclerView.LayoutManager layoutManager;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // private ArrayList<ShopQuickDetails> shopQuickDetails=new ArrayList<>();
    private ArrayList<SubscribedShopsClassJava> subscribedShopsList=new ArrayList<>();
    private Query query;
    private ListenerRegistration registration;

    public NearMe() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_near_me,container,false);
        recyclerView=view.findViewById(R.id.list2);
        recyclerView.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this.getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        myadapter=new subscribedListAdapter(this.getActivity(),subscribedShopsList);
        recyclerView.setAdapter(myadapter);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getFirestoreData();

        super.onActivityCreated(savedInstanceState);

    }
    private void getFirestoreData() {
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        query = db.collection("shops");

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
    public void onDetach() {
        registration.remove();
        super.onDetach();

    }

    @Override
    public void onDestroyView() {
        registration.remove();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        registration.remove();
        super.onDestroy();
    }
}
