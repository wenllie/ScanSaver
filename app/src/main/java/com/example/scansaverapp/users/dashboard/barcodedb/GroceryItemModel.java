package com.example.scansaverapp.users.dashboard.barcodedb;

import android.util.Log;

public class GroceryItemModel {

    String groceryName, groceryMeasurement, groceryPrice, groceryCategory, groceryUPCA, groceryEAN13, groceryBrand, groceryQuantity, groceryDate;

    public GroceryItemModel() {

    }

    public GroceryItemModel(String groceryName, String groceryMeasurement, String groceryPrice, String groceryCategory, String groceryUPCA, String groceryEAN13, String groceryBrand, String groceryQuantity, String groceryDate) {
        this.groceryName = groceryName;
        this.groceryMeasurement = groceryMeasurement;
        this.groceryPrice = groceryPrice;
        this.groceryCategory = groceryCategory;
        this.groceryUPCA = groceryUPCA;
        this.groceryEAN13 = groceryEAN13;
        this.groceryBrand = groceryBrand;
        this.groceryQuantity = groceryQuantity;
        this.groceryDate = groceryDate;
    }

    public String getGroceryName() {
        return groceryName;
    }

    public String getGroceryMeasurement() {
        return groceryMeasurement;
    }

    public String getGroceryPrice() {
        return groceryPrice;
    }

    public String getGroceryCategory() {
        return groceryCategory;
    }

    public String getGroceryUPCA() {
        return groceryUPCA;
    }

    public String getGroceryEAN13() {
        return groceryEAN13;
    }

    public String getGroceryQuantity() {
        return groceryQuantity;
    }

    public String getGroceryBrand() {
        return groceryBrand;
    }

    public String getGroceryDate() {
        return groceryDate;
    }

    public void setGroceryName(String groceryName) {
        this.groceryName = groceryName;
    }

    public void setGroceryMeasurement(String groceryMeasurement) {
        this.groceryMeasurement = groceryMeasurement;
    }

    public void setGroceryPrice(String groceryPrice) {
        this.groceryPrice = groceryPrice;
    }

    public void setGroceryCategory(String groceryCategory) {
        this.groceryCategory = groceryCategory;
    }

    public void setGroceryUPCA(String groceryUPCA) {
        this.groceryUPCA = groceryUPCA;
    }

    public void setGroceryEAN13(String groceryEAN13) {
        this.groceryEAN13 = groceryEAN13;
    }

    public void setGroceryQuantity(String groceryQuantity) {
        this.groceryQuantity = groceryQuantity;
    }

    public void setGroceryBrand(String groceryBrand) {
        this.groceryBrand = groceryBrand;
    }

    public void setGroceryDate(String groceryDate) {
        this.groceryDate = groceryDate;
    }

    public double returnPriceAsFloat(){
        String thisPrice = this.groceryPrice;
        if(thisPrice.equals("N/A"))
            thisPrice = "00.00"; // has to be 00.00 so the return statement will be the substring "0.00"
        Log.d("GroceryItem", thisPrice.substring(1));
        return Double.parseDouble(thisPrice.substring(1));
    }
}
