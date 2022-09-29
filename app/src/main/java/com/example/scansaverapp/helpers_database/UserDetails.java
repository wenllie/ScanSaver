package com.example.scansaverapp.helpers_database;


public class UserDetails {
    public String fullName, gender, birthday, age, phoneNumber, email, customerId;

    public UserDetails() {
    }

    public UserDetails(String fullName, String gender, String birthday, String age, String phoneNumber, String email, String customerId) {
        this.fullName = fullName;
        this.gender = gender;
        this.birthday = birthday;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.customerId = customerId;
    }

}
