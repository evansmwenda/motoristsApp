package com.mwenda.carfix.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mwenda.carfix.R;
import com.mwenda.carfix.adapters.TowingCompanyAdapter;
import com.mwenda.carfix.constants.RetrofitClient;
import com.mwenda.carfix.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    RecyclerView recyclerView;
    private SweetAlertDialog loadingDialog,errorDialog;
    private String user_id,email_address,phone_number,role,company_name,lat,lon;
    List<User> userList;
    SharedPreferences sp ;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        sp=getContext().getSharedPreferences("carfix",MODE_PRIVATE);

        lat = sp.getString("lat","0");
        lon = sp.getString("lon","0");

        //Toast.makeText(getContext(), "latte->"+lat+"\nloone->"+lon, Toast.LENGTH_SHORT).show();

        //getting the recyclerview from xml
        recyclerView = root.findViewById(R.id.recylcerViewHome);
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        errorDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getTowers("towing companies");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
    }

    public  void getTowers(final String search_query){
        if(userList != null){
            userList.clear();
            recyclerView.setAdapter(null);
        }

        //final JSONObject object = new
        // JSONObject();
        loadingDialog.setTitleText("Loading ...");//Processing your request
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();

        errorDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTowersInRadius(lat,lon);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingDialog.dismiss();
                if(response.isSuccessful()){
                    //status code 200,300
                    try {
                        JSONObject jsonObjectReply = new JSONObject(response.body().string());
                        Log.d("defcon", "onResponse: "+jsonObjectReply.toString());
                        boolean status = jsonObjectReply.optBoolean("success",false);
                        Log.d("defcon", "onResponse: status->"+status);

                        if(status){
                            // successful
                            JSONArray jsonArrayMessage = jsonObjectReply.optJSONArray("message");//orders
                            for(int i=0;i<jsonArrayMessage.length();i++){
                                JSONObject jsonObjectData = jsonArrayMessage.getJSONObject(i);//first order in array

                                user_id=jsonObjectData.optString("id");
                                email_address=jsonObjectData.optString("email_address");
                                phone_number=jsonObjectData.optString("phone_number");
                                company_name=jsonObjectData.optString("company_name");

                                userList.add(new User(
                                        user_id,
                                        email_address,
                                        phone_number,
                                        company_name
                                ));

                            }
                            TowingCompanyAdapter towingAdapter = new TowingCompanyAdapter(getContext(), userList);
                            recyclerView.setAdapter(towingAdapter);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    //error occurred->status code 40X
                    Log.d("defcon", "onResponse: fail->"+response.errorBody());
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();

                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getContext(), "An error occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
