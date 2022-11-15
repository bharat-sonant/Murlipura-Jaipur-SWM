package com.vCare.murlipurajaipurswm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 2950;
    SharedPreferences preferences;
    DatabaseReference reference;
    String link, support;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("CITIZEN APP", MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance(Const.path).getReference();

        if (isOnline()) {
            checkUpdate();
            getSupport();
        } else {
            try {
                new AlertDialog.Builder(SplashScreen.this)
                        .setTitle("Error")
                        .setMessage("Internet not available")
                        .setCancelable(false)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            } catch (Exception e) {
                Log.d("TAG", "Show Dialog: " + e.getMessage());
            }
        }
    }

    private void checkUpdate() {
        reference.child("Settings/LatestVersions/CitizenApp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String localVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    if (dataSnapshot != null) {
                        if (dataSnapshot.getValue() != null) {
                            String version = dataSnapshot.getValue().toString();
                            if (!version.equals(localVersion)) {
                                showVersionAlertBox();
                            } else {
                                splashScreen();
                            }
                        } else {
                            showVersionAlertBox();
                        }
                    } else {
                        showVersionAlertBox();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    showVersionAlertBox();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showVersionAlertBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Version Expired");
        builder.setMessage("Your App version is not Matched. Please update your app.");
        builder.setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();

                    try {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(link));
                        startActivity(viewIntent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to Connect Try Again...",
                                Toast.LENGTH_LONG).show();
                        finish();
                        e.printStackTrace();
                    }
                    finish();
                }
        );

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
    }

    private void splashScreen() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferences != null && preferences.getBoolean("LOGIN", false) == true) {
                    Intent i = new Intent(SplashScreen.this, MainScreen.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashScreen.this, Login_Page.class);
                    startActivity(i);
                }

                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }

    private void getSupport() {
        reference.child("Defaults").child("HelpNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (snapshot.hasChild("GooglePlayStoreAppLink")) {
                        link = snapshot.child("GooglePlayStoreAppLink").getValue().toString();
                    }
                    support = snapshot.child("Support").getValue().toString();
                    preferences.edit().putString("SUPPORT NUMBER", support).apply();
                    preferences.edit().putString("PATH", Const.path).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR ", "ERROR: " + error.toString());
            }
        });
    }

}