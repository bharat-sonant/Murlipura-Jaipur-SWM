package com.vCare.murlipurajaipurswm.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vCare.murlipurajaipurswm.R;

public class LocationPage extends Fragment implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;
    SharedPreferences preferences;
    DatabaseReference ref;
    Marker marker = null;
    private GoogleMap mGoogle;
    private final static int LOCATION_REQUEST = 500;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_page, container, false);

       supportMapFragment  = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.googleMap);
            supportMapFragment.getMapAsync(this);

        return view;
    }
   private void getDatabaseDetails(){
        preferences = getContext().getSharedPreferences("CITIZEN APP", Context.MODE_PRIVATE);
        ref = FirebaseDatabase.getInstance(preferences.getString("PATH","")).getReference();
   }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogle =googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
            return;
        }
        mGoogle.setMyLocationEnabled(true);
        mGoogle.getUiSettings().setCompassEnabled(false);
        mGoogle.setBuildingsEnabled(true);
        getVehicleLocation();
    }

    private void getVehicleLocation(){
        getDatabaseDetails();
        ref.child("CurrentLocationInfo").child(preferences.getString("WARD","")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("latLng")){
                    String[] latlngString = snapshot.child("latLng").getValue().toString().split(",");
                    LatLng latLng =new LatLng(Double.parseDouble(latlngString[0]),Double.parseDouble(latlngString[1]));
                    try {
                        int height = 80;
                        int width = 40;
                        BitmapDrawable bitDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.wastevehicle);
                        Bitmap bit = bitDrawable.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(bit, width, height, false);
                        if (marker != null) {
                            mGoogle.clear();
                        }
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("वाहन यहाँ है");
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        marker = mGoogle.addMarker(markerOptions);
                        mGoogle.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}