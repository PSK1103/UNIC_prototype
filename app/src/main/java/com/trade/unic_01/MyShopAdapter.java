package com.trade.unic_01;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trade.unic_01.dataclasses.SubscribedShopsClassJava;

import java.util.ArrayList;

public class MyShopAdapter extends RecyclerView.Adapter<MyShopAdapter.ViewHolder> {
    private ArrayList<SubscribedShopsClassJava> shopsOwned;
    private Context mContext;
    public MyShopAdapter(Context context, ArrayList<SubscribedShopsClassJava> list){
        mContext = context;
        shopsOwned = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvShopName;
        ImageView ivShopPhoto,ivinfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tvshopName);
            ivShopPhoto=itemView.findViewById(R.id.ivShopPhoto);
            ivinfo=itemView.findViewById(R.id.ivInfo);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                                   }
            });
        }
    }
    @NonNull
    @Override
    public MyShopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_shop_list_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyShopAdapter.ViewHolder holder, final int position) {
        holder.itemView.setTag(shopsOwned.get(position));
        holder.tvShopName.setText(shopsOwned.get(position).getShopname());
        Glide.with(mContext).load(shopsOwned.get(position).getShopimage()).into(holder.ivShopPhoto);
        holder.ivinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,ShopProfile.class);
                intent.putExtra("pic",R.drawable.shop1);
                intent.putExtra("shopid",shopsOwned.get(position).getShopid());
                intent.putExtra("Shop name",shopsOwned.get(position).getShopname());
                intent.putExtra("status",0);
                mContext.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return shopsOwned.size();
    }
}
