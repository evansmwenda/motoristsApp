package com.mwenda.carfix.accounts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mwenda.carfix.HomeActivity;
import com.mwenda.carfix.R;
import com.mwenda.carfix.constants.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail,editPass;
    private String email,pass;
    private SweetAlertDialog pDialog;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private FusedLocationProviderClient fusedLocationClient;
    LocationManager mLocationManager;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private static final int PERMISSION_REQUEST_CODE3 =502;
    private static final int REQUEST_CHECK_SETTINGS3 = 0x3;
    SweetAlertDialog errorDialog;
    public double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        sp=getSharedPreferences("carfix",MODE_PRIVATE);
        pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(LoginActivity.this);

        checkLocationPermission();
    }
    public void goToRegister(View v){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    private void initializeViews() {
        editEmail=findViewById(R.id.editEmail);
        editPass=findViewById(R.id.editPass);
    }

    private boolean validateForm() {
        boolean valid = true;

        email = editEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Required");
            editEmail.requestFocus();
            valid = false;
        } else {
            editEmail.setError(null);
        }

        pass = editPass.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            editPass.setError("Required");
            editPass.requestFocus();
            valid = false;
        } else {
            editPass.setError(null);
        }

        return valid;
    }

    public void userLogin(View view) {
        if(!validateForm()){
            return;
        }
        login(email,pass);
    }

    private void login(String email_address, String password) {
        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);//Color.parseColor("#A5DC86")
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .loginUser(email_address,password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                if(response.isSuccessful()){
                    //status code 200,300
                    try {
                        JSONObject jsonObjectReply = new JSONObject(response.body().string());
                        Log.d("defcon", "onResponse: "+jsonObjectReply.toString());
                        boolean status = jsonObjectReply.optBoolean("success",false);
                        Log.d("defcon", "onResponse: status->"+status);

                        if(status){
                            //login successful
                            String reply = jsonObjectReply.optString("reply","Login successful");
                            JSONObject jsonObjectMessage = jsonObjectReply.optJSONObject("message");

                            String user_id = jsonObjectMessage.optString("id","0");
                            String user_email = jsonObjectMessage.optString("email_address","0");
                            String user_company = jsonObjectMessage.optString("company_name","0");
                            String user_phone= jsonObjectMessage.optString("phone_number","0");
                            String user_role= jsonObjectMessage.optString("role","0");

                            //write to shared prefs
                            spEditor = sp.edit();
                            spEditor.clear();
                            spEditor.putString("id",user_id);
                            spEditor.putString("email_address",user_email);
                            spEditor.putString("phone_number",user_phone);
                            spEditor.putString("company_name",user_company);
                            spEditor.putString("lat", String.valueOf(lat));
                            spEditor.putString("lon", String.valueOf(lon));
                            spEditor.putString("role",user_role);
                            spEditor.apply();

                            Toast.makeText(LoginActivity.this, reply, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    //error occurred->status code 40X
                    Log.d("defcon", "onResponse: fail->"+response.errorBody());
                    Toast.makeText(LoginActivity.this, "An error occurred", Toast.LENGTH_LONG).show();

//                    try {
//                        JSONObject jsonObjectEr = new JSONObject(response.errorBody().string());
//                        boolean status = jsonObjectEr.optBoolean("success",false);
//                        if(!status){
//                            //check  response status codes
//
//                            String reply = jsonObjectEr.optJSONObject("reply").optString("msg","An error occurred, please try again");
//                            Toast.makeText(LoginActivity.this, ""+reply, Toast.LENGTH_LONG).show();
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(LoginActivity.this, "An error occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        pDialog.dismiss();
    }

    private boolean checkPerm() {
        //check if permission granted
        return ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    protected void checkLocationPermission() {
        LocationRequest locationRequest5 = LocationRequest.create();
        locationRequest5.setInterval(10000);
        locationRequest5.setFastestInterval(5000);
        locationRequest5.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //get current location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest5);
        //check whether current location settings are satisfied
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

//        determine whether the user's location settings allow location services to create a LocationRequest,
//        as well as how to ask the user for permission to change the location settings if necessary
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if(checkPerm()){
                    Toast.makeText(LoginActivity.this, "location permission on", Toast.LENGTH_SHORT).show();
                    buildLocationRequest();
                    buildLocationCallBack();
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }else{
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE3);
                }
            }
        });


        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        //Toast.makeText(Spareparts.this, "Device location is OFF", Toast.LENGTH_SHORT).show();
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS3);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE3:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        buildLocationRequest();
                        buildLocationCallBack();
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                    }else {
                        //permission denied
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel(
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                            PERMISSION_REQUEST_CODE3);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_CHECK_SETTINGS3:
                if(resultCode== Activity.RESULT_OK){
                    // granted
                    if(checkPerm()){
                        buildLocationRequest();
                        buildLocationCallBack();
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }else{
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE3);
                    }
                }else{
                    //not granted
                    errorDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE);
                    errorDialog.setTitleText("Device location is required for app to work");
                    errorDialog.setCancelable(true);
                    errorDialog.show();
//                Toast.makeText(this, "Turn on device location for best results", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }
    void buildLocationRequest(){
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setSmallestDisplacement(5);
    }

    void buildLocationCallBack(){
        locationCallback=new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location:locationResult.getLocations()){
                    lat= location.getLatitude();
                    lon= location.getLongitude();
                }
            }
        };
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage("You need to allow access to this permission for the app to work")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
