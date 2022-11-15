package com.vCare.murlipurajaipurswm.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vCare.murlipurajaipurswm.Model.NewComplainModel;
import com.vCare.murlipurajaipurswm.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class NewComplain extends Fragment {

    Spinner spinner;
    EditText messageET;
    MaterialButton submitBtn;
    ArrayList<String> typeList = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference ref;
    SharedPreferences preferences;
    ImageView backBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_complain, container, false);

        spinner = view.findViewById(R.id.spinner);
        messageET = view.findViewById(R.id.messageET);
        submitBtn = view.findViewById(R.id.submitBtn);
        backBtn = view.findViewById(R.id.backBtn);

        preferences = getActivity().getSharedPreferences("CITIZEN APP", Context.MODE_PRIVATE);

        database = FirebaseDatabase.getInstance(preferences.getString("PATH", ""));
        ref = database.getReference();

        getComplainType();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTrue()) {
                    showMessage();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contains, new ComplainPage()).commit();
            }
        });

        return  view;
    }


    private void showMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("आपकी शिकायत दर्ज कर लिया गया | ");

        builder.setPositiveButton(Html.fromHtml("OK"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                NewComplainModel model = new NewComplainModel();
                model.setMessage(messageET.getText().toString());
                model.setDate(date);
                model.setComplaintype(spinner.getSelectedItem().toString());
                model.setComlaintnumber(String.valueOf(new Random().nextInt(10000000)));
                model.setNumber(preferences.getString("MOBILE",""));
                model.setZone(preferences.getString("WARD",""));
                model.setAction("1");

                ref.child("ComplaintRequest").push().setValue(model);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contains, new ComplainPage()).commit();
            }
        });

        AlertDialog dialog = builder.create();
        if (!getActivity().isFinishing())
            dialog.show();
    }

    private boolean isTrue() {

        if (messageET.getText().toString().isEmpty()) {
            messageET.setError("Enter Message");
            messageET.requestFocus();
            return false;
        } else if (spinner.getSelectedItem().toString().isEmpty() || spinner.getSelectedItem().toString().equals("--- शिकायत टाइप चुने ---")) {
            View selectedView = spinner.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                spinner.requestFocus();
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setError("error"); // any name of the error will do
                selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
                selectedTextView.setText("please select type"); // actual error message
                spinner.performClick();
                return false;
            }
        }
        return true;
    }

    private void getComplainType() {
        ref.child("Defaults/ComplainType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                typeList.add("--- शिकायत टाइप चुने ---");
                for (DataSnapshot data : snapshot.getChildren()) {
                    typeList.add(data.getValue().toString());
                }
                setSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSpinner() {

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, typeList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }
}