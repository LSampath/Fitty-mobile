package com.example.fitty.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fitty.Adapters.DatabaseHelper;
import com.example.fitty.R;
import com.example.fitty.controllers.RunController;
import com.example.fitty.controllers.StepController;
import com.example.fitty.models.RunningSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        if (preferences.getBoolean(getString(R.string.first_time), true)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        BottomNavigationView bottomNav = findViewById(R.id.navbar_bottom);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        setFragment(HomeFragment.newInstance());
                        break;
                    case R.id.action_pulse:
                        setFragment(HeartRateFragment.newInstance());
                        break;
                    case R.id.action_run:
                        setFragment(RunFragment.newInstance());
                        break;
                    case R.id.action_history:
                        setFragment(HistoryFragment.newInstance());
                        break;
                }
                return true;
            }
        });

        bottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                //
            }
        });

//        remove this
        DatabaseHelper db = new DatabaseHelper(this);
//        long i = RunController.insertSession(db, new RunningSession(1577182315000L, 1577185915000L, 12.456));
//        Toast.makeText(this,  i+"", Toast.LENGTH_LONG).show();
//        RunController.insertSession(db, new RunningSession(1577159155000L, 1577166351234L, 10.46));
//        RunController.insertSession(db, new RunningSession(1577166355000L, 1577184355000L, 12.45));
//        RunController.insertSession(db, new RunningSession(1577179255000L, 1577180155000L, 2.4));



        setFragment(HomeFragment.newInstance());
    }

    private void setFragment(Fragment object) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_container, object);
        fragmentTransaction.commit();

    }
}
