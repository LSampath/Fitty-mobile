package com.example.fitty.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.fitty.R;

public class LoginActivity extends AppCompatActivity {

    private Button submitBtn;

    private EditText nameTxt;
    private EditText ageTxt;
    private EditText heightTxt;
    private EditText weightTxt;
    private RadioGroup genderGroup;
    private RadioButton genderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameTxt = this.findViewById(R.id.activity_login_txt_name);
        ageTxt = this.findViewById(R.id.activity_login_txt_age);
        heightTxt = this.findViewById(R.id.activity_login_txt_height);
        weightTxt = this.findViewById(R.id.activity_login_txt_weight);
        genderGroup = this.findViewById(R.id.activity_login_group);

        submitBtn = findViewById(R.id.activity_login_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int genderId = genderGroup.getCheckedRadioButtonId();
                genderBtn = findViewById(genderId);
                String gender = (String) genderBtn.getText();

                int age = Integer.parseInt(ageTxt.getText().toString());
                float height = Float.parseFloat(heightTxt.getText().toString());
                float weight = Float.parseFloat(weightTxt.getText().toString());
                String name = nameTxt.getText().toString();

                SharedPreferences.Editor prefEditor = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE).edit();
                prefEditor.putString(getString(R.string.pref_name), name);
                prefEditor.putString(getString(R.string.pref_gender), gender);
                prefEditor.putInt(getString(R.string.pref_age), age);
                prefEditor.putFloat(getString(R.string.pref_height), height);
                prefEditor.putFloat(getString(R.string.pref_weight), weight);

                prefEditor.putBoolean(getString(R.string.first_time), false);
                prefEditor.commit();

                LoginActivity.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
