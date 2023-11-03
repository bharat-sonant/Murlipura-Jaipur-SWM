package com.vCare.murlipurajaipurswm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 1000;
    SharedPreferences preferences;
    DatabaseReference reference;
    String support;

    public static final int UPDATE_CODE = 100;
    AppUpdateManager appUpdateManager;
    SharedPreferences.Editor editor;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("CITIZEN APP", MODE_PRIVATE);
        editor = preferences.edit();
        reference = FirebaseDatabase.getInstance(Const.path).getReference();
        editor.putString("PATH", Const.path).apply();

        if (isOnline()) {
            getSupport();
            checkForAppUpdate();
        } else {
            showNoInternetDialog();
        }
    }

    private void checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> task = appUpdateManager.getAppUpdateInfo();

        task.addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            SplashScreen.this,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                            UPDATE_CODE
                    );
                } catch (IntentSender.SendIntentException e) {
//                    Log.e("TAG", "Update Failed 1 : " + e.getMessage());
                }

            } else {
                splashScreen();
            }

        }).addOnFailureListener(e -> {
//                    Log.e("TAG", "Update Failed 2 : " + e);
                    splashScreen();
                }
        );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_CODE) {
            if (resultCode == RESULT_OK) {
                splashScreen();
            } else {
                finishAndRemoveTask();
            }
        } else {
//            Log.e("TAG", "update successfully update 2");
        }
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
    }

    private void splashScreen() {

        new Handler().postDelayed(() -> {
            if (preferences != null && preferences.getBoolean("LOGIN", false)) {
                Intent i = new Intent(SplashScreen.this, MainScreen.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(SplashScreen.this, Login_Page.class);
                startActivity(i);
                finish();
            }
            finish();
        }, SPLASH_SCREEN_TIME_OUT);

    }

    private void getSupport() {
        reference.child("Defaults").child("HelpNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    support = snapshot.child("Support").getValue().toString();
                    editor.putString("SUPPORT NUMBER", support).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR ", "ERROR: " + error.toString());
            }
        });
    }


    private void showNoInternetDialog() {
        try {
            new AlertDialog.Builder(SplashScreen.this)
                    .setTitle("Error")
                    .setMessage("Internet not available")
                    .setCancelable(false)
                    .setNeutralButton("OK", (dialog, which) -> finish()).show();
        } catch (Exception e) {
            Log.d("TAG", "Show Dialog: " + e.getMessage());
        }
    }
}