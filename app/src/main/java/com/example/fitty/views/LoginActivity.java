package com.example.fitty.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fitty.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
