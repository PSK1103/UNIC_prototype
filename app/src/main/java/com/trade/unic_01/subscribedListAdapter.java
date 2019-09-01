package com.trade.unic_01;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.trade.unic_01.dataclasses.SubscribedDataClass;
import com.trade.unic_01.dataclasses.SubscribedShopsClassJava;

import java.util.ArrayList;
import java.util.HashMap;


public class subscribedListAdapter extends RecyclerView.Adapter<subscribedListAdapter.ViewHolder> {
    ArrayList<SubscribedShopsClassJava> shopsSubscribed;
    private Context context1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public subscribedListAdapter(Context context, ArrayList<SubscribedShopsClassJava> list) {
        shopsSubscribed = list;
        context1=context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvShopName;
        ImageView ivShopPhoto,ivinfo,ivSubscribe;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tvshopName);
            ivShopPhoto=itemView.findViewById(R.id.ivShopPhoto);
            ivinfo=itemView.findViewById(R.id.ivInfo);
            ivSubscribe=itemView.findViewById(R.id.ivSubscribe);





            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }


    @NonNull
    @Override
    public subscribedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull subscribedListAdapter.ViewHolder holder, final int position) {
        holder.itemView.setTag(shopsSubscribed.get(position));
        holder.tvShopName.setText(shopsSubscribed.get(position).getShopname());
        Glide
                .with(context1)
                .load(shopsSubscribed.get(position).getShopimage())
                .into(holder.ivShopPhoto);

        //use Glide here to display image
        //holder.ivShopPhoto.setImageResource(shopsSubscribed.get(position).getShopimage());


        holder.ivinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context1,ShopProfile.class);
                Log.d("ShopProfile2","data "+shopsSubscribed.get(position).getShopid());
                intent.putExtra("pic",shopsSubscribed.get(position).getShopimage());
                Log.d("Subscribed","imagelink "+shopsSubscribed.get(position).getShopimage());
                intent.putExtra("imagelink",shopsSubscribed.get(position).getShopimage());
                intent.putExtra("shopid",shopsSubscribed.get(position).getShopid());
                intent.putExtra("status",1);
                context1.startActivity(intent);
            }
        }

        );
        holder.ivSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                HashMap<String,String> dataMap = new HashMap<>();
                dataMap.put("shopid",shopsSubscribed.get(position).getShopid());
                dataMap.put("shopname",shopsSubscribed.get(position).getShopname());
                dataMap.put("imagelink",shopsSubscribed.get(position).getShopimage());
                db.collection("users").document(mAuth.getUid()).collection("subscribed").document(shopsSubscribed.get(position).getShopid()).set(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context1, "Subscribed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context1, "Subscribe Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return shopsSubscribed.size();
    }
}

    /*private Context context;
    private List<ShopQuickDetails> items;
    private int resource;

    public subscribedListAdapter(Context context,int resource,List<ShopQuickDetails> list)
    {
        super(context, R.layout.shop_list_item, list);
        this.context=context;
        this.items=list;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.shop_list_item,parent,true);
        }



        ImageView ivShopPhoto=convertView.findViewById(R.id.ivShopPhoto),ivCart=convertView.findViewById(R.id.ivCart), ivCall=convertView.findViewById(R.id.ivCall), ivInfo=convertView.findViewById(R.id.ivInfo);
        TextView tvShopName=convertView.findViewById(R.id.tvshopName);


        tvShopName.setText(items.get(position).getName());
        //setImage
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //opens phonecall
            }
        });


        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //opens shop profile

            }
        });


        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //opens shop

            }
        });








        return convertView;
    }*/

