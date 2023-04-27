package com.vCare.murlipurajaipurswm.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vCare.murlipurajaipurswm.Model.NewComplainMappingModel;
import com.vCare.murlipurajaipurswm.Model.NewComplainModel;
import com.vCare.murlipurajaipurswm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
    String date, month, year;
    Map<String, Object> map = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contains, new ComplainPage()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(new Date());
        try {
            year = new SimpleDateFormat("yyyy").format(dateFormat.parse(date));
            month = new SimpleDateFormat("MMMM", Locale.US).format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        return view;
    }


    private void showMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("आपकी शिकायत दर्ज कर लिया गया | ");

        builder.setPositiveButton(Html.fromHtml("OK"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                map.put("complaintType", spinner.getSelectedItem().toString());
                map.put("message", messageET.getText().toString());
                map.put("registerBy", "CitizenApp");
                map.put("status", "1");

                ref.child("ComplaintsData").child("Complaints").child(preferences.getString("WARD", ""))
                        .child(year).child(month).child(date)
                        .child(preferences.getString("CARD NUMBER", "")).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                setCount();
                                setMapping();
                            }
                        });

                ref.child("ComplaintsData").child("UserComplaintReference")
                        .child(preferences.getString("CARD NUMBER", ""))
                        .child(date)
                        .setValue(preferences.getString("WARD", "") + "/" + year + "/" + month + "/" + date + "/" + preferences.getString("CARD NUMBER", ""));

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contains, new ComplainPage()).commit();
            }
        });

        AlertDialog dialog = builder.create();
        if (!getActivity().isFinishing())
            dialog.show();
    }

    private void setMapping() {
        NewComplainMappingModel model = new NewComplainMappingModel();
        model.setCard(preferences.getString("CARD NUMBER", ""));
        model.setComplaintDataPath(preferences.getString("WARD", "") + "/" + year + "/" + month + "/" + date + "/" + preferences.getString("CARD NUMBER", ""));
        model.setDate(date);
        model.setComplaintSource("CitizenApp");

        ref.child("ComplaintsData").child("ComplaintsMapping").child("Open").child(preferences.getString("WARD", "")).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                setCount();
            }
        });
    }

    private void setCount() {
        ref.child("ComplaintsData/CountSummary/StatusWise/Open").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int count = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (Objects.equals(data.getKey(), preferences.getString("WARD", ""))) {
                            count = Integer.parseInt(data.getValue() + "");
                        }
                    }
                    count++;
                    ref.child("ComplaintsData").child("CountSummary").child("StatusWise").child("Open").child(preferences.getString("WARD", "")).setValue(count);
                } else {
                    ref.child("ComplaintsData").child("CountSummary").child("StatusWise").child("Open").child(preferences.getString("WARD", "")).setValue("1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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