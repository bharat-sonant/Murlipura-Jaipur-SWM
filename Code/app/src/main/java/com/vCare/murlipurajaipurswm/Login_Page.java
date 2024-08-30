package com.vCare.murlipurajaipurswm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login_Page extends AppCompatActivity {

    EditText cardNumber;
    MaterialButton loginBtn;
    FirebaseDatabase database;
    DatabaseReference ref;
    SharedPreferences preferences;
    String smartCardNumber;
    final int PERMISSION_REQUEST_CODE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        cardNumber = findViewById(R.id.cardNumber);
        loginBtn = findViewById(R.id.loginBtn);

        preferences = getSharedPreferences("CITIZEN APP", MODE_PRIVATE);
        database = FirebaseDatabase.getInstance(preferences.getString("PATH", ""));
        ref = database.getReference();

        loginBtn.setOnClickListener(view -> {
            login();
        });

        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission();
            }
        }

    }

    public void getNotificationPermission(){
        try {
            if (Build.VERSION.SDK_INT > 32) { ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // allow
                }  else {
                    //deny
                }
                return;

        }

    }

    private void login() {
        ProgressDialog dialog = new ProgressDialog(Login_Page.this);
        dialog.setMessage("Please Wait...");
        dialog.show();
        smartCardNumber = "MPZ" + cardNumber.getText().toString();
        ref.child("CardWardMapping/" + smartCardNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("CARD NUMBER", smartCardNumber)
                            .putString("LINE", snapshot.child("line").getValue().toString())
                            .putString("WARD", snapshot.child("ward").getValue().toString())
                            .apply();
                    Intent i = new Intent(Login_Page.this, OtpScreen.class);
                    startActivity(i);
                    finish();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    new android.app.AlertDialog.Builder(Login_Page.this)
                            .setTitle("Alert!")
                            .setMessage("Card Number is not available in our Database")
                            .setCancelable(false).
                            setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", error.toString());
            }
        });
    }
}