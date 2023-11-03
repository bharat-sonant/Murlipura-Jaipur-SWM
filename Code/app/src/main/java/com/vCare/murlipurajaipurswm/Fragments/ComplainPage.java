package com.vCare.murlipurajaipurswm.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vCare.murlipurajaipurswm.Adapter.ComplainAdapter;
import com.vCare.murlipurajaipurswm.Model.ComplainModel;
import com.vCare.murlipurajaipurswm.OtpScreen;
import com.vCare.murlipurajaipurswm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ComplainPage extends Fragment {

    MaterialButton newComplainBtn;
    RecyclerView complainRV;
    LinearLayout linearLayout;
    DatabaseReference ref;
    SharedPreferences preferences;
    int i;
    String date, pathRef;
    ArrayList<ComplainModel> models = new ArrayList<>();
    ComplainAdapter adapter;
    ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complain_page, container, false);

        newComplainBtn = view.findViewById(R.id.newComplainBtn);
        complainRV = view.findViewById(R.id.complainRV);
        linearLayout = view.findViewById(R.id.linearLayout);

        getDataBase();
        hideKeyboard(getActivity());
        getNewComplainList();

        newComplainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contains, new NewComplain()).commit();
            }
        });
        return view;
    }

    private void getDataBase() {
        preferences = getActivity().getSharedPreferences("CITIZEN APP", Context.MODE_PRIVATE);
        ref = FirebaseDatabase.getInstance(preferences.getString("PATH", "")).getReference();
    }

    private void getNewComplainList() {
        setProgressBar("","Please Wait", getContext(),getActivity());
        getDataBase();
        ref.child("ComplaintsData").child("UserComplaintReference").child(preferences.getString("CARD NUMBER", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ArrayList<String>
                            list = new ArrayList<String>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        date = data.getKey();
                        list.add(date);
                    }
                    Collections.sort(list, Collections.reverseOrder());
                    for (int i = 0; i < list.size(); i++) {
                        String dateValues = list.get(i);
                        pathRef = snapshot.child(dateValues).getValue(String.class);
                        showData(pathRef, dateValues);
                    }
                }else {
                    closeDialog(getActivity());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showData(String pathRef, String dateValues) {
        models.clear();
        ref.child("ComplaintsData").child("Complaints").child(pathRef).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SimpleDateFormat", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String date = dateValues;
                    String complainType = snapshot.child("complaintType").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    String message = snapshot.child("message").getValue(String.class);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date1 = simpleDateFormat.parse(date);
                        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
                        date = simpleDateFormat.format(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    models.add(new ComplainModel(message, date, complainType, status));
                }
                adapter = new ComplainAdapter(getActivity(), models);
                complainRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                closeDialog(getActivity());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocusedView = activity.getCurrentFocus();
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProgressBar(String title, String message, Context context, Activity activity) {
        closeDialog(activity);
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (!dialog.isShowing() && !activity.isFinishing()) {
            dialog.show();
        }
    }

    public void closeDialog(Activity activity) {
        try {
            if (dialog != null) {
                if (dialog.isShowing() && !activity.isFinishing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
        }
    }

}