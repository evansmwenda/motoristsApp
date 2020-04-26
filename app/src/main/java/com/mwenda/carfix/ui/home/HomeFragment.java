package com.mwenda.carfix.ui.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    RecyclerView recyclerView;
    private SweetAlertDialog loadingDialog,errorDialog;
    private String user_id,email_address,phone_number,role,company_name;
    List<User> userList;
    private Button btnsearch;
    private EditText editSearch;
    private TextView textWelcome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        //getting the recyclerview from xml
        recyclerView = root.findViewById(R.id.recylcerViewOrders);
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        errorDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);

        btnsearch = root.findViewById(R.id.btnsearch);
        editSearch = root.findViewById(R.id.editSearch);
        textWelcome = root.findViewById(R.id.textWelcome);

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchable = editSearch.getText().toString().trim();
                if(TextUtils.isEmpty(searchable) || !TextUtils.equals(searchable.toLowerCase(),"towing")){
                    Toast.makeText(getContext(), "Enter a valid name to search", Toast.LENGTH_SHORT).show();
                }else{
                    getTowers(searchable);
                }

            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        return root;
    }



//    public void userLogin(View view) {
//        search =
//        if(!TextUtils.isEmpty(search)){
//            Toast.makeText(getContext(), "Enter a search query", Toast.LENGTH_SHORT).show();
//        }
//        getTowers(search);
//    }


    public  void getTowers(final String search_query){
        if(userList != null){
            userList.clear();
            recyclerView.setAdapter(null);
        }
        textWelcome.setVisibility(View.GONE);

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
                .getTowingCompanies(search_query);

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
