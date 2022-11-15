package com.vCare.murlipurajaipurswm.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vCare.murlipurajaipurswm.Adapter.ComplainAdapter;
import com.vCare.murlipurajaipurswm.Model.ComplainModel;
import com.vCare.murlipurajaipurswm.R;

import java.util.ArrayList;

public class ComplainPage extends Fragment {

    MaterialButton newComplainBtn;
    RecyclerView complainRV;
    LinearLayout linearLayout;
    DatabaseReference ref;
    SharedPreferences preferences;
    int i;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_complain_page, container, false);

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
                       .replace(R.id.contains,new NewComplain()).commit();
            }
        });
        return view;
    }

    private void getDataBase() {
        preferences = getActivity().getSharedPreferences("CITIZEN APP",Context.MODE_PRIVATE);
        ref = FirebaseDatabase.getInstance(preferences.getString("PATH","")).getReference();
    }

    private void getNewComplainList() {
        getDataBase();
        Query query = ref.child("ComplaintRequest").orderByChild("number");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ComplainModel> models = new ArrayList<>();
                i = 1;
                for (DataSnapshot snap : snapshot.getChildren()){
                    Log.e("data",""+snap.toString());
                    if (snap.hasChild("number")){
                        if (snap.child("number").getValue().toString().equals(preferences.getString("MOBILE", ""))) {
                            linearLayout.setVisibility(View.VISIBLE);
                            models.add(new ComplainModel(String.valueOf(i),
                                    snap.child("date").getValue().toString(),
                                    snap.child("complaintype").getValue().toString(),
                                    snap.child("action").getValue().toString()));
                            i++;
                        }
                        ComplainAdapter adapter = new ComplainAdapter(getActivity(),models);
                        complainRV.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        linearLayout.setVisibility(View.INVISIBLE);
    }

    public static void hideKeyboard(Activity activity) {
        try{
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocusedView = activity.getCurrentFocus();
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}