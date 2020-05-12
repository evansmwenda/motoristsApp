package com.mwenda.carfix.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mwenda.carfix.R;
import com.mwenda.carfix.constants.Constants;
import com.mwenda.carfix.constants.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText editEmail,editPhone,editPass,editCPass;
    private String email,phone,pass,cpass;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initiateViews();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
    }

    public void goToLogin(View v){
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void initiateViews() {
        editPass=findViewById(R.id.editPassword);
        editPhone=findViewById(R.id.editPhone);
        editCPass=findViewById(R.id.editConfirmPassword);
        editEmail=findViewById(R.id.editEmail);
    }

    private boolean validateForm() {
        boolean valid = true;



        email = editEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Required");
            editEmail.requestFocus();
            valid = false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Invalid E-mail Address format");
            editEmail.requestFocus();
            valid=false;
        }
        else {
            editEmail.setError(null);
        }

        phone = editPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Required");
            editPhone.requestFocus();
            valid = false;
        } else if(!Constants.checkPhone(phone)){
            //invalid phone input
            editPhone.setError("Invalid phone number");
            editPhone.requestFocus();
            valid=false;
        }else {
            editPhone.setError(null);
        }


        pass = editPass.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            editPass.setError("Required");
            editPass.requestFocus();
            valid = false;
        } else {
            editPass.setError(null);
        }

        cpass = editCPass.getText().toString();
        if (TextUtils.isEmpty(cpass)) {
            editCPass.setError("Required");
            editCPass.requestFocus();
            valid = false;
        }else if(!TextUtils.equals(cpass,pass)) {
            editCPass.setError("Password mismatch");
            editCPass.requestFocus();
            valid = false;
        }else{
            editCPass.setError(null);
        }

        return valid;
    }

    public void userRegister(View view) {
        //user clicked register button->validate form
        if(!validateForm()){
            return;
        }else{
            signUp(email,phone,pass,cpass);
        }
    }

    private void signUp(final String email_address, String phone_number, final String password, String confirm_password) {
        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);//Color.parseColor("#A5DC86")
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        String role="1";
        String company_name="";

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .registerUser(email_address,phone_number,password,confirm_password,role,company_name);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                if(response.isSuccessful()){
                    //user will receive otp
                    try {
                        JSONObject jsonObjectReply = new JSONObject(response.body().string());

                        boolean status = jsonObjectReply.optBoolean("success",false);
                        Log.d("defcon", "onResponse: status->"+status);


                        String reply = jsonObjectReply.optString("message","User registered successfully");
                        Toast.makeText(RegisterActivity.this, reply, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        intent.putExtra("email_address",email_address);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    //an error occurred
                    Toast.makeText(RegisterActivity.this, "An error occurred, please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Log.d("defcon", "onFailure: "+t.getMessage());
                Toast.makeText(RegisterActivity.this, "An error occurred, please try again", Toast.LENGTH_LONG).show();

            }
        });
    }
}
