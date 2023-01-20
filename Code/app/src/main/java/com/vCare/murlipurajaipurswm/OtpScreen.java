package com.vCare.murlipurajaipurswm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OtpScreen extends AppCompatActivity {

    MaterialButton verifyBtn, getOptBtn;
    EditText enterNumberET, enterOtpET;
    TextView mobileNumberTV, resendBtn, counterTV;
    LinearLayout getOtpLayout, verifyLayout;

    FirebaseAuth mAuth;

    String verificationId, mobileNumber, ward, lineNo, cardNumber;
    SharedPreferences preferences;

    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_screen);

        verifyBtn = findViewById(R.id.verifyBtn);
        getOtpLayout = findViewById(R.id.getOtpLayout);
        verifyLayout = findViewById(R.id.verifyLayout);
        getOptBtn = findViewById(R.id.getOtpBtn);
        enterNumberET = findViewById(R.id.enterNumberET);
        enterOtpET = findViewById(R.id.enterOtpET);
        counterTV = findViewById(R.id.counterTV);

        mobileNumberTV = findViewById(R.id.mobileNumberTV);
        resendBtn = findViewById(R.id.resendBtn);

        mAuth = FirebaseAuth.getInstance();

        preferences = getSharedPreferences("CITIZEN APP", MODE_PRIVATE);
        database = FirebaseDatabase.getInstance(preferences.getString("PATH", ""));
        ref = database.getReference();

        ward = preferences.getString("WARD", "");
        lineNo = preferences.getString("LINE", "");
        cardNumber = preferences.getString("CARD NUMBER", "");

        getOptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumber = enterNumberET.getText().toString();
                if (mobileNumber.length() == 10) {
                    getOtpLayout.setVisibility(View.GONE);
                    verifyLayout.setVisibility(View.VISIBLE);
                    mobileNumberTV.setText(mobileNumber);
                    sendVerificationCode();
                } else {
                    enterNumberET.setError("Enter Valid Number");
                    enterNumberET.requestFocus();
                }

            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignInCode();
            }
        });

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterTV.setVisibility(View.VISIBLE);
                resendBtn.setVisibility(View.GONE);
                sendVerificationCode();
            }
        });
    }
    private void sendVerificationCode() {

        new CountDownTimer(50000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                counterTV.setText(f.format(min) + ":" + f.format(sec));
            }

            public void onFinish() {
                counterTV.setText("00:00");
                counterTV.setVisibility(View.GONE);
                resendBtn.setVisibility(View.VISIBLE);
            }
        }.start();

        String phone = "+91" + enterNumberET.getText().toString();
        if (phone.isEmpty()) {
            enterNumberET.setError("Phone number is required");
            enterNumberET.requestFocus();
            return;
        } else if (phone.length() < 10) {
            enterNumberET.setError("Please enter a valid phone number");
            enterNumberET.requestFocus();
            return;
        } else {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this, mCallback);
        }

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d("data", "onVerificationCompleted: check");
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.e("data", "onVerificationCompleted: check A " + e.toString());
            Toast.makeText(OtpScreen.this, "To many attempts. Please try again later. ", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.e("data", "onVerificationCompleted: check B " + s);
            verificationId = s;
        }
    };

    private void verifySignInCode() {
        if (verificationId.length() > 0) {
            if (enterOtpET.getText().length() >= 1) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enterOtpET.getText().toString());
                signInWithPhoneAuthCredential(credential);
            } else {
                Log.e("data", "FAILED 1");
            }
        } else {
            Log.e("data", "FAILED 2");
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("mobile", mobileNumber);
                    ref.child("Houses").child(ward).child(lineNo).child(cardNumber).updateChildren(map);

                    String token = FirebaseInstanceId.getInstance().getToken();
                    ref.child("CardWardMapping").child(cardNumber).child("Token").setValue(token);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("MOBILE", mobileNumber);
                    editor.putBoolean("LOGIN", true);
                    editor.apply();


                    Intent i = new Intent(OtpScreen.this, MainScreen.class);
                    startActivity(i);
                } else {
                    Log.e("data", "Failed");
                }

            }
        });
    }
}