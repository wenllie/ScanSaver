package com.example.scansaveradmin.helpers;

public class AdminDetails {

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAge() {
        return age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String fullName, gender, birthday, age, phoneNumber, email;

    public AdminDetails() {
    }

    public AdminDetails(String fullName, String gender, String birthday, String age, String phoneNumber, String email) {
        this.fullName = fullName;
        this.gender = gender;
        this.birthday = birthday;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

}
