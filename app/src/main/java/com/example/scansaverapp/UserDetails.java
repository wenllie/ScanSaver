package com.example.scansaverapp;


public class UserDetails {
    public String fullName, gender, birthday, age, phoneNumber, email, password;

    public UserDetails() {
    }

    public UserDetails(String fullName, String gender, String birthday, String age, String phoneNumber, String email, String password) {
        this.fullName = fullName;
        this.gender = gender;
        this.birthday = birthday;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }

}
