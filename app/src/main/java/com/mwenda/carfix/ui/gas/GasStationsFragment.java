package com.mwenda.carfix.ui.gas;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class GasStationsFragment extends Fragment {

    private GasStationsViewModel gasStationsViewModel;
    RecyclerView recyclerView;
    private SweetAlertDialog loadingDialog, errorDialog;
    private String user_id, email_address, phone_number, distance, company_name,lat,lon,image;
    List<User> userList;
    SharedPreferences sp ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gasStationsViewModel =
                ViewModelProviders.of(this).get(GasStationsViewModel.class);


        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_gas, container, false);

        final TextView textView = root.findViewById(R.id.text_gas);
        gasStationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        sp=getContext().getSharedPreferences("carfix",MODE_PRIVATE);

        lat = sp.getString("lat","0");
        lon = sp.getString("lon","0");

        //getting the recyclerview from xml
        recyclerView = root.findViewById(R.id.recylcerViewGas);
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

        String role = "3";

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getResourcesInRadius(role,lat,lon);

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
                                distance=jsonObjectData.optString("distance");
                                image=jsonObjectData.optString("image");

                                userList.add(new User(
                                        user_id,
                                        email_address,
                                        phone_number,
                                        company_name,
                                        distance,
                                        image
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
