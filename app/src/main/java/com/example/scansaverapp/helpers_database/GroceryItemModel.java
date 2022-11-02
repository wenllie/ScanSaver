package com.example.scansaverapp.helpers_database;

import android.util.Log;

public class GroceryItemModel {

    String groceryName, groceryMeasurement, groceryPrice, groceryCategory, groceryUPCA, groceryEAN13, groceryBrand, groceryQuantity, groceryDate, groceryTotalItemPrice, customerId, groceryImgUrl;

    public GroceryItemModel() {

    }

    public GroceryItemModel(String groceryName, String groceryMeasurement, String groceryPrice, String groceryCategory, String groceryUPCA, String groceryEAN13, String groceryBrand, String groceryQuantity, String groceryDate,String groceryTotalItemPrice, String customerId, String groceryImgUrl) {
        this.groceryName = groceryName;
        this.groceryMeasurement = groceryMeasurement;
        this.groceryPrice = groceryPrice;
        this.groceryCategory = groceryCategory;
        this.groceryUPCA = groceryUPCA;
        this.groceryEAN13 = groceryEAN13;
        this.groceryBrand = groceryBrand;
        this.groceryQuantity = groceryQuantity;
        this.groceryDate = groceryDate;
        this.groceryTotalItemPrice = groceryTotalItemPrice;
        this.customerId = customerId;
        this.groceryImgUrl = groceryImgUrl;
    }

    public String getGroceryName() {
        return groceryName;
    }

    public void setGroceryName(String groceryName) {
        this.groceryName = groceryName;
    }

    public String getGroceryMeasurement() {
        return groceryMeasurement;
    }

    public void setGroceryMeasurement(String groceryMeasurement) {
        this.groceryMeasurement = groceryMeasurement;
    }

    public String getGroceryPrice() {
        return groceryPrice;
    }

    public void setGroceryPrice(String groceryPrice) {
        this.groceryPrice = groceryPrice;
    }

    public String getGroceryCategory() {
        return groceryCategory;
    }

    public void setGroceryCategory(String groceryCategory) {
        this.groceryCategory = groceryCategory;
    }

    public String getGroceryUPCA() {
        return groceryUPCA;
    }

    public void setGroceryUPCA(String groceryUPCA) {
        this.groceryUPCA = groceryUPCA;
    }

    public String getGroceryEAN13() {
        return groceryEAN13;
    }

    public void setGroceryEAN13(String groceryEAN13) {
        this.groceryEAN13 = groceryEAN13;
    }

    public String getGroceryBrand() {
        return groceryBrand;
    }

    public void setGroceryBrand(String groceryBrand) {
        this.groceryBrand = groceryBrand;
    }

    public String getGroceryQuantity() {
        return groceryQuantity;
    }

    public void setGroceryQuantity(String groceryQuantity) {
        this.groceryQuantity = groceryQuantity;
    }

    public String getGroceryDate() {
        return groceryDate;
    }

    public void setGroceryDate(String groceryDate) {
        this.groceryDate = groceryDate;
    }

    public String getGroceryTotalItemPrice() {
        return groceryTotalItemPrice;
    }

    public void setGroceryTotalItemPrice(String groceryTotalItemPrice) {
        this.groceryTotalItemPrice = groceryTotalItemPrice;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getGroceryImgUrl() {
        return groceryImgUrl;
    }

    public void setGroceryImgUrl(String groceryImgUrl) {
        this.groceryImgUrl = groceryImgUrl;
    }

    public double returnPriceAsFloat(){
        String thisPrice = this.groceryPrice;
        if(thisPrice.equals("N/A"))
            thisPrice = "00.00"; // has to be 00.00 so the return statement will be the substring "0.00"
        Log.d("GroceryItem", thisPrice.substring(1));
        return Double.parseDouble(thisPrice.substring(1));
    }
}
