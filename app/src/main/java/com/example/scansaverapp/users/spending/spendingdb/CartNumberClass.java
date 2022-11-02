package com.example.scansaverapp.users.spending.spendingdb;

import com.example.scansaverapp.users.dashboard.barcodedb.MainCategoryClass;

import java.util.List;

public class CartNumberClass {

    String cartNum;
    List<MainCategorySpendingClass> categoryClassList;

    public CartNumberClass(String cartNum, List<MainCategorySpendingClass> categoryClassList) {
        this.cartNum = cartNum;
        this.categoryClassList = categoryClassList;
    }

    public String getCartNum() {
        return cartNum;
    }

    public void setCartNum(String cartNum) {
        this.cartNum = cartNum;
    }

    public List<MainCategorySpendingClass> getCategoryClassList() {
        return categoryClassList;
    }

    public void setCategoryClassList(List<MainCategorySpendingClass> categoryClassList) {
        this.categoryClassList = categoryClassList;
    }
}
