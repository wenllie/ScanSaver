package com.example.scansaverapp.users.dashboard.barcodedb;

import com.example.scansaverapp.helpers_database.GroceryItemModel;

import java.util.List;

public class MainCategoryClass {

    String title;
    List<GroceryItemModel> groceryList;

    public MainCategoryClass(String title, List<GroceryItemModel> groceryList) {
        this.title = title;
        this.groceryList = groceryList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<GroceryItemModel> getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(List<GroceryItemModel> groceryList) {
        this.groceryList = groceryList;
    }
}
