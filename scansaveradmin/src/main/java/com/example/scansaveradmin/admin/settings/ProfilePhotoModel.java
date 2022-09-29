package com.example.scansaveradmin.admin.settings;

public class ProfilePhotoModel {

    String profileUrl;

    public ProfilePhotoModel() {

    }

    public ProfilePhotoModel(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
