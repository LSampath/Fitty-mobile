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
import com.example.fitty.models.AppData;

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

                SharedPreferences.Editor prefEditor = getSharedPreferences(AppData.SHARED_PREF, MODE_PRIVATE).edit();
                prefEditor.putString(AppData.NAME, name);
                prefEditor.putString(AppData.GENDER, gender);
                prefEditor.putInt(AppData.AGE, age);
                prefEditor.putFloat(AppData.HEIGHT, height);
                prefEditor.putFloat(AppData.WEIGHT, weight);

                prefEditor.putBoolean(AppData.FIRST_TIME, false);
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
