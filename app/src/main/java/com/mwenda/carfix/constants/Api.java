package com.mwenda.carfix.constants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {
//    String BASE_URL = "https://skytoptrial.000webhostapp.com/functions/";

    @FormUrlEncoded
    @POST("registeruser.php")
    Call<ResponseBody> registerUser(
            @Field("email_address") String email_address,
            @Field("phone_number") String phone_number,
            @Field("password") String password,
            @Field("confirm_password") String confirm_password,
            @Field("role") String role,
            @Field("company_name") String company_name
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> loginUser(
            @Field("email_address") String email_address,
            @Field("password") String password
    );

    @GET("loan/lenders")
    Call<ResponseBody> getLenderList(@Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("auth/verify-otp")
    Call<ResponseBody> verifyOTP(
            @Field("otp_code") String otp_code
    );

}
