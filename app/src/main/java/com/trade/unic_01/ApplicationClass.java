package com.trade.unic_01;

import android.app.Application;

import java.util.ArrayList;

public class ApplicationClass extends Application {
    public static ArrayList<ShopQuickDetails> shopsSubscribed;
    public static ArrayList<Product> newestetc;
    @Override
    public void onCreate() {
        super.onCreate();

        shopsSubscribed=new ArrayList<ShopQuickDetails>();
        shopsSubscribed.add(new ShopQuickDetails());
        shopsSubscribed.add(new ShopQuickDetails());
        shopsSubscribed.add(new ShopQuickDetails());
        shopsSubscribed.get(0).setName("Shoperific");
        shopsSubscribed.get(1).setName("Fourskin");
        shopsSubscribed.get(2).setName("Pedalphelia");
        shopsSubscribed.get(0).imageid=R.drawable.shop1;
        shopsSubscribed.get(1).imageid=R.drawable.shop2;
        shopsSubscribed.get(2).imageid=R.drawable.shop3;
        newestetc=new ArrayList<Product>();

        newestetc.add(new Product("Rs 145","Fogg Deo","nfjnnrfnfujvnfjnvjfnvjfn nfnvfjnv fjnvjfnv ",R.drawable.product1));
        newestetc.add(new Product("Rs 170","Axe Signature","nfjnnrfnfujvnfjnvjfnvjfn nfnvfjnv fjnvjfnv ",R.drawable.product2));
        newestetc.add(new Product("Rs 220","Denver","nfjnnrfnfujvnfjnvjfnvjfn nfnvfjnv fjnvjfnv ",R.drawable.product3));




    }
}
