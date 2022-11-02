package com.example.scansaveradmin.admin.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemePref {

    SharedPreferences sharedPreferences;

    public ThemePref(Context context) {

        sharedPreferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE);

    }

    //method for save the state
    public void setNightModeState(int state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("NightMode", state);
        editor.commit();

    }

    //method to load night mode state
    public int loadNightModeState() {

        int state = sharedPreferences.getInt("NightMode", 1);
        return state;
    }
}
