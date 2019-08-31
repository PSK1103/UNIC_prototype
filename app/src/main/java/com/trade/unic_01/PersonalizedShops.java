package com.trade.unic_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonalizedShops extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ViewFlipper vfOffers;
    private Switch switchViewFlipper;
    RecyclerView mRecyclerView;
    newestOnsaleAdapter myadapter;
    String shopname,shoplocality,shopid,imagelink;
    ImageView shopkeeperpic;
    TextView Name,Locality;
    Spinner spin;
    Button MyOrders;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalized_shops);
        Name=findViewById(R.id.shopName);
        Locality=findViewById(R.id.shopkeeperadd);
        MyOrders=findViewById(R.id.MyOrders);
        MyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(PersonalizedShops.this,My_orders.class);
                startActivity(i);
                
            }
        });
        shopkeeperpic=findViewById(R.id.ShopkeeperPhoto);
        shopid=getIntent().getStringExtra("shopid");
        shopname=getIntent().getStringExtra("Shop name");
        shoplocality=getIntent().getStringExtra("Shop locality");
        spin=findViewById(R.id.Spinner1);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spin.setAdapter(adapter);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection("shops").document(shopid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        shopname = doc.getString("shopname");
                        shoplocality = doc.getString("shoplocality");
                        imagelink = doc.getString("imagelink");
                    }
                }
            }
        });
        String ownerimglink = "https://imgur.com/gallery/GQGJo5K";
        Glide
                .with(this)
                .load(ownerimglink)
                .into(shopkeeperpic);
        //add shopkeepers photo
        Name.setText(shopname);
        Locality.setText(shoplocality);

        int images[]= {R.drawable.offer1,R.drawable.offer2,R.drawable.offer3};
        switchViewFlipper=findViewById(R.id.switchViewFlipper);
        switchViewFlipper.setTextOn("On");
        switchViewFlipper.setTextOff("Off");
        vfOffers=findViewById(R.id.vfOffer);
        switchViewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchViewFlipper.isChecked())
                    vfOffers.setVisibility(View.GONE);
                else
                    vfOffers.setVisibility(View.VISIBLE);

            }
        });
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.newestetc);
        mRecyclerView.setLayoutManager(layoutManager);
        myadapter=new newestOnsaleAdapter(PersonalizedShops.this, ApplicationClass.newestetc);
        mRecyclerView.setAdapter(myadapter);



        /*for(int i=0;i<images.length;i++)
        {
            flipperImages(images[i]);
        }*/
        for(int image:images){
            flipperImages(image);
        }


    }
    public void flipperImages(int image){
        ImageView imageView=new ImageView(this);
        imageView.setBackgroundResource(image);
        vfOffers.addView(imageView);
        vfOffers.setFlipInterval(3000);
        vfOffers.setAutoStart(true);
        vfOffers.setInAnimation(this,android.R.anim.slide_in_left);
        vfOffers.setOutAnimation(this,android.R.anim.slide_out_right);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
