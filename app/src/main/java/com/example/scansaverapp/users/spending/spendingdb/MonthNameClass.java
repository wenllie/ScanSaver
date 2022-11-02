package com.example.scansaverapp.users.spending.spendingdb;

import java.util.List;

public class MonthNameClass {

    String monthName;
    List<CartNumberClass> monthNameList;

    public MonthNameClass(String monthName, List<CartNumberClass> monthNameList) {
        this.monthName = monthName;
        this.monthNameList = monthNameList;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public List<CartNumberClass> getMonthNameList() {
        return monthNameList;
    }

    public void setMonthNameList(List<CartNumberClass> monthNameList) {
        this.monthNameList = monthNameList;
    }
}
