package com.trade.unic_01;

public class ShopQuickDetails {
    private String name;
    private String imageLink;
    private String ownerId;
    private  String shopId;
    public int imageid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    public ShopQuickDetails(String name, String imageLink, String ownerId, String shopId, int  imageid) {
        this.name = name;
        this.imageLink = imageLink;
        this.ownerId = ownerId;
        this.shopId = shopId;
        this.imageid = imageid;
    }

    public ShopQuickDetails() {
    }
}
