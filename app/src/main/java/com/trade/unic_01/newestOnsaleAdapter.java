package com.trade.unic_01;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class newestOnsaleAdapter extends RecyclerView.Adapter<newestOnsaleAdapter.ViewHolder> {
    private ArrayList<Product> shopproducts;
    private Context context1;

    public newestOnsaleAdapter(Context context, ArrayList<Product> list) {
        shopproducts = list;
        context1=context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductname, tvProductPrice,tvProductDescription;
        ImageView ivProductPhoto;
        Button btnaddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductname=itemView.findViewById(R.id.tvProductName);

            tvProductPrice=itemView.findViewById(R.id.tvPrice);
            ivProductPhoto=itemView.findViewById(R.id.ivProductPhoto);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }


    @NonNull
    @Override
    public newestOnsaleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newest_onsale_etc_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull newestOnsaleAdapter.ViewHolder holder, final int position) {
        holder.itemView.setTag(shopproducts.get(position));
        holder.tvProductname.setText(shopproducts.get(position).getName());
        holder.tvProductPrice.setText(shopproducts.get(position).getPrice());

        holder.ivProductPhoto.setImageResource(shopproducts.get(position).imageid);

    }
    @Override
    public int getItemCount() {
        return shopproducts.size();
    }

    }
   /* private Context context;
    private List<Product> items;

    public newestOnsaleAdapter(Context context,List<Product> list)
    {
        super(context, R.layout.shop_list_item, list);
        this.context=context;
        this.items=list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.newest_onsale_etc_list,parent,false);
        }



        ImageView ivProductPhoto=convertView.findViewById(R.id.ivProductPhoto);
        Button btnAddtoCart=convertView.findViewById(R.id.btnAddtoCart);
        TextView tvProductName=convertView.findViewById(R.id.tvProductName), tvPrice=convertView.findViewById(R.id.tvPrice),tvDescription=convertView.findViewById(R.id.tvDescription);

       //get Product object here

        tvProductName.setText(items.get(position).getCompany()+" "+items.get(position).getName());
        tvPrice.setText("Rs:"+items.get(position).getPrice());
        tvDescription.setText(items.get(position).getDescription());
        //setImage

       btnAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //adds to cart page
            }
        });










        return convertView;
    }*/

