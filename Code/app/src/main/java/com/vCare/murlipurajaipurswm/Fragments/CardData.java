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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;

public class CardData extends Fragment {

    Spinner monthSpinner;
    Spinner yearSpinner;
    RecyclerView scanCardRv;
    MaterialButton okBtn;
    DatabaseReference ref;
    SharedPreferences preferences;
    LinearLayout linearLayout;
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

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        Log.e("data", "" + currentMonth + "  " + currentYear);

        setMonthSpinner();
        setYearSpinner();
        getScanData();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScanData();
//                getRandomScanTime();
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
                Log.e("data", "SNAP " + snapshot.getValue());
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
                                        dateFormat = new SimpleDateFormat("dd MMM yyyy");
                                        date = dateFormat.format(newDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    time = data.child(preferences.getString("CARD NUMBER", "")).child("scanTime").getValue().toString();
                                    Log.e("data", "time " + time);
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
                            getRandomDate(date2);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date newDate = null;
                            try {
                                newDate = dateFormat.parse(date2);
                                dateFormat = new SimpleDateFormat("dd MMM yyyy");
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
                        getRandomDate(monthDate);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = simpleDateFormat.parse(monthDate);
                            simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
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

    private void getRandomDate(String date2) {
        dataBase();
        String setDate = date2;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(setDate);
            Date setDate2 = sdf.parse("2022-11-26");

            assert date1 != null;
            if (date1.before(setDate2)) {
                Log.e("data", "DATES 1: " + setDate);
            } else {
                Log.e("data", "DATES 2: " + setDate);
                ref.child("WasteCollectionInfo").child(preferences.getString("WARD", ""))
                        .child(year).child(month).child(setDate)
                        .child("LineStatus").child(preferences.getString("LINE", ""))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String startTime = snapshot.child("start-time").getValue() + "";
                                String endTime = snapshot.child("end-time").getValue() + "";

                                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                try {
                                    Date date1 = format.parse(startTime);
                                    Date date2 = format.parse(endTime);

                                    long difference = date2.getTime() - date1.getTime();
                                    long diffMinutes = difference / (60 * 1000) % 60;

                                    if (diffMinutes < 1) {
                                        String latLng = preferences.getString("LATLNG", "");

                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("latLng", latLng);
                                        map.put("scanBy", "-1");
                                        map.put("scanTime", endTime);

                                        ref.child("HousesCollectionInfo")
                                                .child(preferences.getString("WARD", ""))
                                                .child(year).child(month)
                                                .child(setDate).child(preferences.getString("CARD NUMBER", "")).setValue(map);

                                    } else if (diffMinutes > 1) {
                                        setRandomTime(diffMinutes, startTime, setDate);
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setRandomTime(long diffMinutes, String startTime, String setDate) {
        int i = 1;
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (i = 1; i <= diffMinutes; i++) {
            arrayList.add(i);
        }
        Random random = new Random();
        int RandomNumber = arrayList.get(random.nextInt(arrayList.size()));

        Log.e("data", "RANDOM " + RandomNumber);

        Long value = (long) RandomNumber;

        Log.e("data", "Minutes " + startTime);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date1 = format.parse(startTime);
            long minutes = date1.getMinutes();
            Log.e("data", "date " + date1);
            Log.e("data", "getMinutes " + minutes);

            long addMinutes = value + minutes;

            Log.e("data", "addMinutes " + addMinutes);

            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            Date date = df.parse(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MINUTE, (int) addMinutes);
            String newTime = df.format(cal.getTime());
            String latLng = preferences.getString("LATLNG", "");

            HashMap<String, String> map = new HashMap<>();
            map.put("latLng", latLng);
            map.put("scanBy", "-1");
            map.put("scanTime", newTime);

            ref.child("HousesCollectionInfo")
                    .child(preferences.getString("WARD", ""))
                    .child(year).child(month)
                    .child(setDate).child(preferences.getString("CARD NUMBER", "")).setValue(map);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setMonthSpinner() {
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};

        Calendar cal = Calendar.getInstance();
        String month1 = monthName[cal.get(Calendar.MONTH)];
        month = month1;

        Log.e("data", "MONTH " + month);

        String mnth[] = {month1, "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        LinkedHashSet<String> stringSet = new LinkedHashSet<>(Arrays.asList(mnth));
        String[] filteredArray = stringSet.toArray(new String[0]);

        ArrayAdapter<String> monthadapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, filteredArray);
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

        year = String.valueOf(thisYear);
        Log.e("data", "YEAR " + year);

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

    @Override
    public void onStart() {
        setMonthSpinner();
        setYearSpinner();
        super.onStart();
    }
}