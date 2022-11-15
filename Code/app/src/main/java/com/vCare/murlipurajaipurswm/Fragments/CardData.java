package com.vCare.murlipurajaipurswm.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vCare.murlipurajaipurswm.Adapter.CardDataAdapter;
import com.vCare.murlipurajaipurswm.Model.ScanCardDataModel;
import com.vCare.murlipurajaipurswm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CardData extends Fragment {

    Spinner monthSpinner;
    Spinner yearSpinner;
    RecyclerView scanCardRv;
    MaterialButton okBtn;
    DatabaseReference ref;
    SharedPreferences preferences;
    LinearLayout linearLayout;
    ArrayList<String> monthList = new ArrayList<>();
    ArrayList<String> yearList = new ArrayList<>();
    ArrayList<String> fullMonthDate = new ArrayList<>();
    String year, month;
    ArrayList<ScanCardDataModel> arrayList = new ArrayList<>();
    String list;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_data, container, false);

        monthSpinner = view.findViewById(R.id.monthSpinner);
        scanCardRv = view.findViewById(R.id.scanCardRv);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        linearLayout = view.findViewById(R.id.linearLayout);
        okBtn = view.findViewById(R.id.okBtn);

        setMonthSpinner();
        setYearSpinner();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScanData();
            }
        });


        return view;
    }

    private void dataBase() {
        preferences = getActivity().getSharedPreferences("CITIZEN APP", Context.MODE_PRIVATE);
        ref = FirebaseDatabase.getInstance(preferences.getString("PATH", "")).getReference();
    }

    @SuppressLint("NewApi")
    private void getScanData() {
        dataBase();
        fullMonthDate.clear();
        String monthSet = month;
        String yearSet = year;
        SimpleDateFormat format = new SimpleDateFormat("MMMM");
        try {
            Date newDate = format.parse(monthSet);
            format = new SimpleDateFormat("MM");
            monthSet = format.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int getYear = Integer.parseInt(yearSet);
        int getMonth = Integer.parseInt(monthSet);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.clear();

        cal.set(getYear, getMonth - 1, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < daysInMonth; i++) {
            fullMonthDate.add(fmt.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        ref.child("HousesCollectionInfo").child(preferences.getString("WARD", "")).child(year).child(month).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                String date = "";
                String time = "";
                int temp = 0;
                int flag = 0;
                if (snapshot.exists()) {
                    for (int i = temp; i < fullMonthDate.size(); i++) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                            list = fullMonthDate.get(i);
                            if (list.equals(data.getKey())) {
                                if (data.hasChild(preferences.getString("CARD NUMBER", ""))) {
                                    linearLayout.setVisibility(View.VISIBLE);
                                    date = data.getKey();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date newDate = null;
                                    try {
                                        newDate = dateFormat.parse(date);
                                        dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                        date = dateFormat.format(newDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    time = data.child(preferences.getString("CARD NUMBER", "")).child("scanTime").getValue().toString();
                                    arrayList.add(new ScanCardDataModel(date, time));
                                    temp = i + 1;
                                    flag = 0;
                                    break;
                                }
                            } else {
                                flag = 1;
                            }
                        }
                        if (flag == 1) {
                            linearLayout.setVisibility(View.VISIBLE);
                            String date2 = list;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date newDate = null;
                            try {
                                newDate = dateFormat.parse(date2);
                                dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                date2 = dateFormat.format(newDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            arrayList.add(new ScanCardDataModel(date2, "......"));
                        }
                    }
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < fullMonthDate.size(); i++) {
                        String monthDate = fullMonthDate.get(i);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = simpleDateFormat.parse(monthDate);
                            simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            monthDate = simpleDateFormat.format(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        arrayList.add(new ScanCardDataModel(monthDate, "......"));
                    }
                }
                scanCardRv.setVisibility(View.VISIBLE);
                CardDataAdapter cardDataAdapter = new CardDataAdapter(getActivity(), arrayList);
                scanCardRv.setAdapter(cardDataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("data", "ERROR " + error);
            }
        });
        scanCardRv.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
    }

    private void setMonthSpinner() {
        monthList.clear();

        monthList.add("January");
        monthList.add("February");
        monthList.add("March");
        monthList.add("April");
        monthList.add("May");
        monthList.add("June");
        monthList.add("July");
        monthList.add("August");
        monthList.add("September");
        monthList.add("October");
        monthList.add("November");
        monthList.add("December");

        ArrayAdapter<String> monthadapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, monthList);
        monthadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthadapter);
        monthadapter.notifyDataSetChanged();
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                month = "" + adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setYearSpinner() {

        yearList.clear();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar preYear = Calendar.getInstance();
        preYear.add(Calendar.YEAR, -1);
        int previousYear = preYear.get(Calendar.YEAR);
        for (int i = thisYear; i >= previousYear; i--) {
            yearList.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearAdapter.notifyDataSetChanged();
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                year = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}