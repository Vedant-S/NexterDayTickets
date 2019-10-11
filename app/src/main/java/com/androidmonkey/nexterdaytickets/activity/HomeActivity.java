package com.androidmonkey.nexterdaytickets.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.androidmonkey.nexterdaytickets.R;
import com.androidmonkey.nexterdaytickets.fragment.AccountFragment;
import com.androidmonkey.nexterdaytickets.fragment.EventsFragment;
import com.androidmonkey.nexterdaytickets.fragment.TicketsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    //UI Declarations
    BottomNavigationView homeBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeBottomNavigation = findViewById(R.id.homeBottomNavigation);
        homeBottomNavigation.setOnNavigationItemSelectedListener(navigationItemReselectedListener);
        homeBottomNavigation.setSelectedItemId(R.id.bot_nav_events);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutHome,new EventsFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.bot_nav_events:
                            selectedFragment = new EventsFragment();
                            break;
                        case R.id.bot_nav_tickets:
                            selectedFragment = new TicketsFragment();
                            break;
                        case R.id.bot_nav_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutHome,selectedFragment).commit();
                    return true;
                }
            };
}
