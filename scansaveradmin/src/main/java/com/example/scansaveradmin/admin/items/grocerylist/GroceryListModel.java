package com.example.scansaveradmin.admin.items.grocerylist;

public class GroceryListModel {

    public String groceryName, groceryMeasurement, groceryPrice, groceryCategory, grocerySubCategory, groceryBrand, groceryUPCA, groceryEAN13, groceryImgUrl;

    public GroceryListModel() {

    }

    public GroceryListModel(String groceryName, String groceryMeasurement, String groceryPrice, String groceryCategory, String grocerySubCategory, String groceryBrand, String groceryUPCA, String groceryEAN13, String groceryImgUrl) {
        this.groceryName = groceryName;
        this.groceryMeasurement = groceryMeasurement;
        this.groceryPrice = groceryPrice;
        this.groceryCategory = groceryCategory;
        this.grocerySubCategory = grocerySubCategory;
        this.groceryBrand = groceryBrand;
        this.groceryUPCA = groceryUPCA;
        this.groceryEAN13 = groceryEAN13;
        this.groceryImgUrl = groceryImgUrl;
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

    public String getGroceryBrand() {
        return groceryBrand;
    }

    public String getGroceryUPCA() {
        return groceryUPCA;
    }

    public String getGroceryEAN13() {
        return groceryEAN13;
    }

    public String getGroceryImgUrl() {
        return groceryImgUrl;
    }

    public String getGrocerySubCategory() {
        return grocerySubCategory;
    }
}
