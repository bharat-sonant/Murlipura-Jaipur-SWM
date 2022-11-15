package com.vCare.murlipurajaipurswm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.PolyUtil;
import com.vCare.murlipurajaipurswm.Model.FormDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class FormActivity extends AppCompatActivity {
    EditText userNameET;
    EditText userMobileET;
    MaterialButton submit_btn, location_btn;
    TextView userLocationET;
    FirebaseDatabase database;
    DatabaseReference ref;
    StorageReference storageRef;
    SharedPreferences preferences;
    FusedLocationProviderClient mFusedLocationProviderClient;
    int PERMISSION_ID = 44;
    boolean click = false;

    JSONObject boundariesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        userNameET = findViewById(R.id.userNameET);
        userMobileET = findViewById(R.id.userMobileET);
        submit_btn = findViewById(R.id.submit_btn);
        userLocationET = findViewById(R.id.userLocationET);
        location_btn = findViewById(R.id.location_btn);

        preferences = getSharedPreferences("CITIZEN APP", MODE_PRIVATE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FormActivity.this);

        fetchWardBoundariesData();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });

        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click = true;
                getLastLocation();
            }
        });
    }

    private void submitData() {
        database = FirebaseDatabase.getInstance(preferences.getString("PATH",""));
        ref = database.getReference().child("FormData");

        String name = userNameET.getText().toString();
        String mobile = userMobileET.getText().toString();
        String address = preferences.getString("ADDRESS", "");
        String latitude = preferences.getString("LATITUDE", "");
        String longitude = preferences.getString("LONGITUDE", "");
        String ward = preferences.getString("WARD","");

        if (isTrue()) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("NAME",userNameET.getText().toString())
                    .putString("MOBILE",userMobileET.getText().toString())
                    .putBoolean("LOGIN",true)
                    .commit();

            FormDataModel data = new FormDataModel();
            data.setUserName(name);
            data.setMobile(mobile);
            data.setAddress(address);
            data.setLatitude(latitude);
            data.setLongitude(longitude);
            data.setWard(ward);

            ref.child(mobile).setValue(data);

            Intent i = new Intent(FormActivity.this,MainScreen.class);
            startActivity(i);

        }
    }

    private boolean isTrue() {
        String name = userNameET.getText().toString();
        String mobile = userMobileET.getText().toString();

        if (name.isEmpty()) {
            userNameET.setError("Fill Name");
            userNameET.requestFocus();
            return false;
        } else if (mobile.isEmpty()) {
            userMobileET.setError("Fill Mobile");
            userMobileET.requestFocus();
            return false;
        } else if (mobile.length() < 10) {
            userMobileET.setError("Enter Valid Mobile Number");
            userMobileET.requestFocus();
            return false;
        } else if (!click) {
            new AlertDialog.Builder(FormActivity.this)
                    .setTitle("Location")
                    .setMessage("Please Click on Turn on Location Button")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
            return false;
        } else if (userLocationET.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please click on Location Button", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            String latitude = String.valueOf(location.getLatitude());
                            String longitude = String.valueOf(location.getLongitude());

                            try {
                                Geocoder geocoder = new Geocoder(FormActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),2);

                                String address = addresses.get(0).getAddressLine(0);
                                userLocationET.setText(addresses.get(0).getAddressLine(0));

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("LATITUDE", latitude);
                                editor.putString("LONGITUDE", longitude);
                                editor.putString("ADDRESS",address);
                                editor.apply();

                                if (addresses.size() > 0) {
                                    wardFromAvailableLatLng(location);
                                } else {
                                    Toast.makeText(FormActivity.this, "Location Failed", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("data", "ERROR: LOCATION 1 " + e.getMessage());
                            }
                        }
                    }
                });
            } else {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        } else {
            requestPermissions();
        }
    }

    private void wardFromAvailableLatLng(Location location) {
        try {
            Iterator<String> iterator = boundariesData.keys();
            boolean isIterate = false;

            while (iterator.hasNext()) {
                try {
                    String key = iterator.next();

                    JSONArray tempLatLngArray = new JSONArray(String.valueOf(boundariesData.get(key)));

                    ArrayList<LatLng> latLngOfBoundaryArrayList = new ArrayList<>();

                    for (int i = 0; i <= tempLatLngArray.length() - 1; i++) {

                        String[] latlngArray = String.valueOf(tempLatLngArray.get(i)).split(",");

                        latLngOfBoundaryArrayList.add(new LatLng(Double.parseDouble(latlngArray[0].trim()), Double.parseDouble(latlngArray[1].trim())));

                        if (i == tempLatLngArray.length() - 1) {

                            if (PolyUtil.containsLocation(new LatLng(location.getLatitude(), location.getLongitude()), latLngOfBoundaryArrayList, true)) {
                                isIterate = true;
                                String ward = key;
                                Log.e("data","WARD "+ward);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("WARD", ward).apply();
                                break;
                            }
                        }
                    }
                    if (isIterate) break;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!isIterate) {
                SharedPreferences.Editor editor = preferences.edit();
                Log.e("data","WARD NOT FOUND");
                editor.putString("WARD", "WARD NOT FOUND");
                editor.apply();
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());

    }

    private final LocationCallback mLocationCallBack = new LocationCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            String latitude = String.valueOf(mLastLocation.getLatitude());
            String longitude = String.valueOf(mLastLocation.getLongitude());

            try {
                Geocoder geocoder = new Geocoder(FormActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),2);

                userLocationET.setText(addresses.get(0).getAddressLine(0));

                Log.e("data","ADDRESS 2: 0: "+addresses.get(0).getAddressLine(0));

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("LATITUDE", latitude);
                editor.putString("LONGITUDE",longitude);
                editor.putString("ADDRESS",addresses.get(0).getAddressLine(0));
                editor.apply();

                if (addresses.size() > 0) {
                    wardFromAvailableLatLng(mLastLocation);
                } else {
                    Toast.makeText(FormActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("data", "ERROR: LOCATION 2 " + e.getMessage());
            }


        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    public void fetchWardBoundariesData() {
        storageRef = FirebaseStorage.getInstance().getReference();

        storageRef.child("Sikar/Defaults/BoundariesLatLng.json").getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                long fileCreationTime = storageMetadata.getCreationTimeMillis();
                long fileDownloadTime = preferences.getLong("BoundariesLatLngDownloadTime", 0);
                if (fileDownloadTime != fileCreationTime) {
                    try {
                        File localeFile = File.createTempFile("images", "jpg");
                        storageRef.child("Sikar/Defaults/BoundariesLatLng.json").getFile(localeFile)
                                .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                                        try {
                                            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localeFile)));
                                            StringBuilder sb = new StringBuilder();
                                            String str;
                                            while ((str = br.readLine()) != null) {
                                                sb.append(str);
                                            }
                                            boundariesData = new JSONObject(sb.toString());
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("BoundariesLatLng", sb.toString())
                                                    .putLong("BoundariesLatLngDownloadTime", fileCreationTime)
                                                    .apply();

                                        } catch (Exception e) {
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("data", "Error 3: " + e.getMessage());
                                    }
                                });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        boundariesData = new JSONObject(preferences.getString("BoundariesLatLng", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("data", "ERROR 4: " + e.getMessage());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("data", "ERROR 5: " + e.getMessage());
            }
        });
    }
}