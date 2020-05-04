package com.mwenda.carfix.constants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

    @FormUrlEncoded
    @POST("alltowers.php")
    Call<ResponseBody> getTowingCompanies(
            @Field("search_query") String search_query
    );


    //gets all towing companies, hospitals,police stations,gas stations
    //based on the role supplied that are nearest to motorist
    //roles can be 1-4 : 1->normal users
    //2->towing stations 3->gas stations 4->hospitals 5->police stations
    @FormUrlEncoded
    @POST("myresources.php")
    Call<ResponseBody> getResourcesInRadius(
            @Field("role") String role,
            @Field("lat") String lat,
            @Field("lng") String lng
    );



    @FormUrlEncoded
    @POST("auth/verify-otp")
    Call<ResponseBody> verifyOTP(
            @Field("otp_code") String otp_code
    );

}
