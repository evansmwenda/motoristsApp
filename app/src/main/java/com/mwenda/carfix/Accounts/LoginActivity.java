package com.mwenda.carfix.Accounts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail,editPass;
    private String email,pass;
    private SweetAlertDialog pDialog;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        sp=getSharedPreferences("carfix",MODE_PRIVATE);
        pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
}
