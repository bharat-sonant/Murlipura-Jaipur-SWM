package com.vCare.murlipurajaipurswm.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vCare.murlipurajaipurswm.R;
import com.vCare.murlipurajaipurswm.SplashScreen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePage extends Fragment {

    CircleImageView driverImg, helperImg;
    ImageView customerCare, logoutBtn;
    TextView driverName, helperName, driverNumber, helperNumber, userName, supervisorContact;
    String date, year, month;
    DatabaseReference ref;
    FirebaseDatabase database;
    SharedPreferences preferences;
    AlertDialog.Builder ad;
    ConstraintLayout btn_pay_history;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        driverImg = view.findViewById(R.id.driverImg);
        driverName = view.findViewById(R.id.driverName);
//        driverNumber = view.findViewById(R.id.driverNumber);
        helperImg = view.findViewById(R.id.helperImg);
        helperName = view.findViewById(R.id.helperName);
//        helperNumber = view.findViewById(R.id.helperNumber);
        supervisorContact = view.findViewById(R.id.supervisorContact);
        customerCare = view.findViewById(R.id.customerCare);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        btn_pay_history = view.findViewById(R.id.btn_pay_history);

        // get User Name
//        getUserName();

        // get Driver and Helper Details
        getDetails();

        // get Supervisor Contact Number
        getSupervisorContactNumber();

        // Call to customer care
        customerCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+preferences.getString("SUPPORT NUMBER","")));

                if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},10);
                }else {
                    try {
                        getActivity().startActivity(callIntent);
                    }catch (android.content.ActivityNotFoundException e){}
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad = new AlertDialog.Builder(getActivity());
                ad.setCancelable(false);
                ad.setMessage("Are you Sure you want to logout");
                ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        removeToken();
                        preferences = getContext().getSharedPreferences("CITIZEN APP",Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.clear().apply();
                        Intent intent = new Intent(getActivity(), SplashScreen.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ad.show();
            }
        });

        btn_pay_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contains, new PaymentHistory()).commit();
            }
        });

        return view;

    }

    private void removeToken() {
        ref.child("CardWardMapping").child(preferences.getString("CARD NUMBER", ""))
                .child("Token").removeValue();
    }

    private void getDataBase() {
        preferences = getContext().getSharedPreferences("CITIZEN APP", Context.MODE_PRIVATE);
        database = FirebaseDatabase.getInstance(preferences.getString("PATH", ""));
        ref = database.getReference();
    }

    private void getSupervisorContactNumber(){
        getDataBase();
        ref.child("Settings").child("WardSupervisorContact").child(preferences.getString("WARD","")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String contactNumber = snapshot.getValue().toString();
                    preferences.edit().putString("SUPERVISOR CONTACT", contactNumber).apply();
                    supervisorContact.setText(contactNumber);
                    superVisorCall();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDetails() {
        getDataBase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(new Date());
        try {
            year = new SimpleDateFormat("yyyy").format(dateFormat.parse(date));
            month = new SimpleDateFormat("MMMM", Locale.US).format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ref.child("WasteCollectionInfo/" + preferences.getString("WARD", "")).child(year).child(month).child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String driverId = snapshot.child("WorkerDetails/driver").getValue().toString().split(",")[ snapshot.child("WorkerDetails/driver").getValue().toString().split(",").length-1];
                    String helperId = snapshot.child("WorkerDetails/helper").getValue().toString().split(",")[ snapshot.child("WorkerDetails/helper").getValue().toString().split(",").length-1];

                    ref.child("Employees/" + driverId).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("UseRequireInsteadOfGet")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child("GeneralDetails").child("name").getValue().toString();
                            String mobile = snapshot.child("GeneralDetails").child("mobile").getValue().toString();
                            String url = snapshot.child("GeneralDetails").child("profilePhotoURL").getValue().toString();
                            preferences.edit().putString("DRIVER NUMBER",mobile).apply();
                            driverName.setText(name);
//                            driverNumber.setText(mobile);
//                            driverCall();
                            try {
                                Glide.with(Objects.requireNonNull(getActivity()))
                                        .load(url)
                                        .error("ERROR")
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .placeholder(R.drawable.man)
                                        .into(driverImg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    ref.child("Employees/" + helperId).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("UseRequireInsteadOfGet")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String name = snapshot.child("GeneralDetails").child("name").getValue().toString();
                            String mobile = snapshot.child("GeneralDetails").child("mobile").getValue().toString();
                            String url = snapshot.child("GeneralDetails").child("profilePhotoURL").getValue().toString();
                            preferences.edit().putString("HELPER NUMBER",mobile).apply();
                            helperName.setText(name);
//                            helperNumber.setText(mobile);
//                            helperCall();
                            try {
                                Glide.with(Objects.requireNonNull(getActivity()))
                                        .load(url)
                                        .error("ERROR")
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .placeholder(R.drawable.man)
                                        .into(helperImg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "ERROR:getUserName: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void superVisorCall() {
        supervisorContact.setMovementMethod(LinkMovementMethod.getInstance());
        supervisorContact.setText(supervisorContact.getText().toString(), TextView.BufferType.SPANNABLE);
        Spannable spannable = (Spannable) supervisorContact.getText();
        final ClickableSpan myClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                phoneCall(preferences.getString("SUPERVISOR CONTACT",""));
            }
        };
        spannable.setSpan(myClick,0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK),0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

//    private void helperCall() {
//        helperNumber.setMovementMethod(LinkMovementMethod.getInstance());
//        helperNumber.setText(helperNumber.getText().toString(), TextView.BufferType.SPANNABLE);
//        Spannable spannable = (Spannable) helperNumber.getText();
//        final ClickableSpan myClick = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View view) {
//                phoneCall(preferences.getString("HELPER NUMBER",""));
//            }
//        };
//        spannable.setSpan(myClick,0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(Color.BLUE),0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//    }

//    private void driverCall() {
//        driverNumber.setMovementMethod(LinkMovementMethod.getInstance());
//        driverNumber.setText(driverNumber.getText().toString(), TextView.BufferType.SPANNABLE);
//        Spannable spannable = (Spannable) driverNumber.getText();
//        final ClickableSpan myClick = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View view) {
//                phoneCall(preferences.getString("DRIVER NUMBER",""));
//            }
//        };
//        spannable.setSpan(myClick,0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(Color.BLUE),0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//    }

    private void phoneCall(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+number));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},10);
        }else{
            try {
                getActivity().startActivity(callIntent);
            }catch (android.content.ActivityNotFoundException e){}
        }
    }

//    private void getUserName() {
//        getDataBase();
//
//        ref.child("Houses/" + preferences.getString("WARD", "") + "/" + preferences.getString("LINE", "") + "/" + preferences.getString("CARD NUMBER", "")).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                String name = snapshot.child("name").getValue(String.class);
//                String mobile = snapshot.child("mobile").getValue(String.class);
//
//                if (!preferences.getString("NAME", "").isEmpty()) {
//                    Log.e("data", "SUCCESS");
//                    userName.setText(preferences.getString("NAME", ""));
//                } else {
//                    Log.e("data", "FAILED");
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString("NAME", name).putString("MOBILE", mobile).apply();
//                    userName.setText(name);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "ERROR:getUserName: " + error, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


}