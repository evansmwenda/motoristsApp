package com.mwenda.carfix.Accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mwenda.carfix.R;

public class RegisterTowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tow);
    }

    public void goToLogin(View v){
        Intent intent = new Intent(RegisterTowActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
