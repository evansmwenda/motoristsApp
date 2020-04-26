package com.mwenda.carfix.constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mwenda.carfix.accounts.LoginActivity;

import java.util.regex.Pattern;

public class Constants {
    public static boolean isConnected=false;

    public static boolean checkPhone(final String phone){
        String passRegex = "^[0-9]{10,}+$";
        Pattern pat = Pattern.compile(passRegex);
        if (phone == null)
            return false;
        return pat.matcher(phone).matches();
    }

    public static void logout(Context context){
        SharedPreferences preferences =context.getSharedPreferences("mlipia", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove("id");
        editor.remove("email");
        editor.remove("id_number");
        editor.remove("fullname");
        editor.remove("token");
        editor.remove("version_code");
        editor.apply();

        Intent intent  = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity) context).finish();

    }
}