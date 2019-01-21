package com.t0p47.imagesliderpinchzoom.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 01Laptop on 28.02.2018.
 */

public class SessionManager {

    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME="ImageGallery";

    //Keys
    //Current img folder
    private static final String KEY_CURRENT_IMG_FOLDER = "CurrentImgFolder";

    //FirstStart
    private static final String KEY_FIRST_START = "firstStart";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setCurrentImgFolder(String filepath){
        editor.putString(KEY_CURRENT_IMG_FOLDER, filepath);

        editor.commit();
    }

    public String getCurrentImgFolder(){
        return pref.getString(KEY_CURRENT_IMG_FOLDER,null);
    }

    public void setFirstStart(boolean firstStart){
        editor.putBoolean(KEY_FIRST_START,true);

        editor.commit();
    }

    public boolean isFirstStart(){
        return pref.getBoolean(KEY_FIRST_START,false);
    }
}
