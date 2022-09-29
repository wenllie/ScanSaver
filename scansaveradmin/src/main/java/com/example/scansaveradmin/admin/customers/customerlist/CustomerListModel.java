package com.example.scansaveradmin.admin.customers.customerlist;

public class CustomerListModel {

    public String fullName, birthday, age, gender, email, phoneNumber, customerId;

    public CustomerListModel() {

    }

    public CustomerListModel(String fullName, String birthday, String age, String gender, String email, String phoneNumber, String customerId) {
        this.fullName = fullName;
        this.birthday = birthday;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCustomerId() {
        return customerId;
    }
}
