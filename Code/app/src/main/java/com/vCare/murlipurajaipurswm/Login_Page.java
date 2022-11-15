package com.vCare.murlipurajaipurswm;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        cardNumber = findViewById(R.id.cardNumber);
        loginBtn = findViewById(R.id.loginBtn);

        preferences = getSharedPreferences("CITIZEN APP", MODE_PRIVATE);
        database = FirebaseDatabase.getInstance(preferences.getString("PATH",""));
        ref = database.getReference();

        loginBtn.setOnClickListener(view -> {
            login();
        });

    }

    private void login() {
        ProgressDialog dialog = new ProgressDialog(Login_Page.this);
        dialog.setMessage("Please Wait...");
        dialog.show();
        smartCardNumber = "MPZ" + cardNumber.getText().toString();
        ref.child("CardWardMapping").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SharedPreferences.Editor editor = preferences.edit();
                if (snapshot.exists()) {
                    if (snapshot.hasChild(smartCardNumber)) {
                        editor.putString("CARD NUMBER", smartCardNumber)
                                .putString("LINE", snapshot.child(smartCardNumber).child("line").getValue().toString())
                                .putString("WARD", snapshot.child(smartCardNumber).child("ward").getValue().toString())
                                .putBoolean("LOGIN",true)
                                .apply();
                        String token = FirebaseInstanceId.getInstance().getToken();
                        ref.child("CardWardMapping").child(smartCardNumber).child("Token").setValue(token);
                        Intent i = new Intent(Login_Page.this, MainScreen.class);
                        startActivity(i);
                        dialog.dismiss();

                    } else if (!snapshot.hasChild(smartCardNumber)) {
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
                        Log.e("data", "FAILED");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", error.toString());
            }
        });
    }
}