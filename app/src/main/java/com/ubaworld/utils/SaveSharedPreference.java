package com.ubaworld.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.ubaworld.utils.Constants.PRE_CHILD;

public class SaveSharedPreference {

    private static SharedPreferences preferences;

    public static void saveStringToUserDefaults(Context context, String key, String value) {
        preferences = context.getSharedPreferences(PRE_CHILD, Context.MODE_PRIVATE);

        LogUtils.e("Utils", "Saving ---> " + key + " ---> " + value + Utils.logLine());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringFromUserDefaults(Context context, String key) {
        LogUtils.e("Utils", "Get ---> " + key + Utils.logLine());
        preferences = context.getSharedPreferences(PRE_CHILD, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

}
