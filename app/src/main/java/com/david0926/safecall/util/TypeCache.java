package com.david0926.safecall.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.david0926.safecall.model.TypeModel;


public class TypeCache {

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setType(Context context, TypeModel model) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor
                .putString("type_name", model.getName())
                .putString("type_voice", model.getVoice())
                .putString("type_desc", model.getDesc())
                .apply();
    }

    public static TypeModel getType(Context context) {
        TypeModel model = new TypeModel();
        model.setName(getSharedPreferences(context).getString("type_name", "선택해주세요"));
        model.setVoice(getSharedPreferences(context).getString("type_voice", "voice"));
        model.setDesc(getSharedPreferences(context).getString("type_desc", "desc"));
        return model;
    }
}
