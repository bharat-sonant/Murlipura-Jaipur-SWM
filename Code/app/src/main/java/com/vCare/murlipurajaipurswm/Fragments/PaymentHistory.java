package com.vCare.murlipurajaipurswm.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vCare.murlipurajaipurswm.Adapter.PaymentHistoryAdapter;
import com.vCare.murlipurajaipurswm.Model.PaymentHistoryModel;
import com.vCare.murlipurajaipurswm.R;

import java.util.ArrayList;

public class PaymentHistory extends Fragment {
    PaymentHistoryAdapter historyAdapter;
    RecyclerView paymentHisRcy;
    TextView tv_noData;
    ProgressBar progress_bar;
    DatabaseReference ref;
    FirebaseDatabase database;
    SharedPreferences preferences;
    ArrayList<PaymentHistoryModel> paymentModel = new ArrayList<>();
    ImageView back_btn;
    String chequeStatus;
    String declinedReason;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contains, new HomePage()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_history_page_layout, container, false);
        initData(view);
        return view;
    }

    private void initData(View view) {
        paymentHisRcy = view.findViewById(R.id.paymentHisRcy);
        tv_noData = view.findViewById(R.id.tv_noData);
        progress_bar = view.findViewById(R.id.progress_bar);
        back_btn = view.findViewById(R.id.back_btn);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contains, new HomePage()).commit();
            }
        });

        getTransactionHistory();
    }

    private void getDataBase() {
        preferences = getContext().getSharedPreferences("CITIZEN APP", Context.MODE_PRIVATE);
        database = FirebaseDatabase.getInstance(preferences.getString("PATH", ""));
        ref = database.getReference();
    }

    private void getTransactionHistory() {
        progress_bar.setVisibility(View.VISIBLE);
        getDataBase();
        ref.child("PaymentCollectionInfo/PaymentTransactionHistory/" + preferences.getString("CARD NUMBER", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    paymentModel.clear();
                    for (@NonNull DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            for (@NonNull DataSnapshot snap : dataSnapshot.getChildren()) {
                                if (snap.exists()) {
                                    for (@NonNull DataSnapshot data : snap.getChildren()) {
                                        if (data.exists()) {
                                            for (@NonNull DataSnapshot dataSnap : data.getChildren()) {
                                                if (dataSnap.exists()) {
                                                    String transactionDateTime = dataSnap.child("transactionDateTime").getValue(String.class);
                                                    String transactionAmount = dataSnap.child("transactionAmount").getValue() + "";
                                                    String retrievalReferenceNo = dataSnap.child("retrievalReferenceNo").getValue(String.class);
                                                    String paymentCollectionByName = dataSnap.child("paymentCollectionByName").getValue(String.class);
                                                    String payMethod = dataSnap.child("payMethod").getValue(String.class);
                                                    String merchantTransactionId = dataSnap.child("merchantTransactionId").getValue(String.class);
                                                    String paidMonthYear = dataSnap.child("monthYear").getValue(String.class);
                                                    paymentModel.add(new PaymentHistoryModel(transactionDateTime, transactionAmount, retrievalReferenceNo, paymentCollectionByName, payMethod, merchantTransactionId, "Paid", paidMonthYear, ""));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    paymentHisRcy.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                    historyAdapter = new PaymentHistoryAdapter(getActivity(), paymentModel);
                    paymentHisRcy.setAdapter(historyAdapter);
                } else {
                    progress_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("PaymentCollectionInfo/PaymentViaCheque/" + preferences.getString("CARD NUMBER", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("VCare", " Cheque " + snapshot.toString());
                if (snapshot.getValue() != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Log.e("VCare", " Cheque " + snapshot1.toString());
                        Log.e("VCare", " Cheque key" + snapshot1.getKey());
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                            Log.e("VCare", " Cheque " + snapshot2.toString());
                            Log.e("VCare", " Cheque key" + snapshot2.getKey());
                            Log.e("VCare", " Cheque key" + snapshot2.child("monthYear").getValue().toString());
                            if (snapshot2.hasChild("monthYear")) {
                                chequeStatus = snapshot2.child("status").getValue().toString();
                                if (chequeStatus.equals("Pending") || chequeStatus.equals("Declined")) {
                                    String merchantTransactionId = snapshot2.child("merchantTransactionId").getValue().toString();
                                    String transactionDateTime = snapshot2.child("collectedDate").getValue().toString();
                                    String transactionAmount = snapshot2.child("amount").getValue().toString();
                                    String payMethod = "By Cheque";
                                    String paidMonthYear = snapshot2.child("monthYear").getValue().toString();
                                    String paymentCollectionByName = snapshot2.child("collectedByName").getValue().toString();
                                    if (snapshot2.hasChild("declinedReason")) {
                                        declinedReason = snapshot2.child("declinedReason").getValue().toString();
                                    }
                                    paymentModel.add(new PaymentHistoryModel(transactionDateTime, transactionAmount, "", paymentCollectionByName, payMethod, merchantTransactionId, chequeStatus, paidMonthYear, declinedReason));
                                }
                            }
                        }
                    }
                    paymentHisRcy.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                    historyAdapter = new PaymentHistoryAdapter(getActivity(), paymentModel);
                    paymentHisRcy.setAdapter(historyAdapter);

                } else {
                    if (paymentModel.size() == 0) {
                        tv_noData.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);
                    } else {
                        tv_noData.setVisibility(View.GONE);
                        progress_bar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
