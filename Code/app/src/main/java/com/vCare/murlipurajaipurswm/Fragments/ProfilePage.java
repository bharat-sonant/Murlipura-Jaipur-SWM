package com.vCare.murlipurajaipurswm.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.vCare.murlipurajaipurswm.R;

public class ProfilePage extends Fragment {


    EditText mobileNumberET;
    EditText cardNumberET;
    SharedPreferences sharedPreferences;
    DatabaseReference ref;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        mobileNumberET = view.findViewById(R.id.mobileNumberET);
        cardNumberET = view.findViewById(R.id.cardNumberET);

        getData();

        return view;
    }

    private void getData() {
        sharedPreferences = getContext().getSharedPreferences("CITIZEN APP", Context.MODE_PRIVATE);
        String mobile = sharedPreferences.getString("MOBILE","");
        String cardNumber = sharedPreferences.getString("CARD NUMBER","");

        mobileNumberET.setText(mobile);
        cardNumberET.setText(cardNumber);

    }
}