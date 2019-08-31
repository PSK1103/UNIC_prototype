package com.trade.unic_01;

import java.util.Date;

public class Product {
    private String company;
    private String price;
    private String name;
    private String description;
    public int imageid;

    public Product(String price, String name, String description, int imageid) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.imageid = imageid;
    }

    private String categoryName;


    private String objectId;

    private String ItemCode;


    //All constructors


    public Product() {
    }


    // getters and setters






    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }



    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String category) {
        this.categoryName = category;
    }
}