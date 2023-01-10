package com.vCare.murlipurajaipurswm;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vCare.murlipurajaipurswm.Fragments.CardData;
import com.vCare.murlipurajaipurswm.Fragments.ComplainPage;
import com.vCare.murlipurajaipurswm.Fragments.HomePage;
import com.vCare.murlipurajaipurswm.Fragments.ProfilePage;

public class MainScreen extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.homeBtn);

    }
    HomePage homePage = new HomePage();
    ProfilePage profilePage = new ProfilePage();
    ComplainPage complainPage = new ComplainPage();
    CardData cardData = new CardData();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeBtn:
                getSupportFragmentManager().beginTransaction().replace(R.id.contains, homePage).commit();
                return true;

            case R.id.profileBtn:
                getSupportFragmentManager().beginTransaction().replace(R.id.contains, profilePage).commit();
                return true;

            case R.id.complaintsBtn:
                getSupportFragmentManager().beginTransaction().replace(R.id.contains, complainPage).commit();
                return true;

            case R.id.scanCardData:
                getSupportFragmentManager().beginTransaction().replace(R.id.contains, cardData).commit();
                return true;
        }
        return false;
    }
}