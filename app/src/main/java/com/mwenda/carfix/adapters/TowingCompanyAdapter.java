package com.mwenda.carfix.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mwenda.carfix.R;
import com.mwenda.carfix.models.User;

import java.util.List;

import static com.mwenda.carfix.constants.RetrofitClient.BASE_URL;

public class TowingCompanyAdapter extends RecyclerView.Adapter<TowingCompanyAdapter.TowingViewHolder> {
    public User userModel;
    private Context context;
    private List<User> userModelList;

    public TowingCompanyAdapter(Context context,List<User> userModelList){
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public TowingCompanyAdapter.TowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.tower_item, null);
        return new TowingCompanyAdapter.TowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TowingCompanyAdapter.TowingViewHolder holder, final int position) {
        userModel = userModelList.get(position);

//        Math.round(miles* 100.0) / 100.0;
        //check if less than 1 mile
        Float miles  = Float.parseFloat(userModel.getDistance().substring(0,7));
        String distance = miles < 1 ?
                String.format("%s Metres Away",Math.round(miles*1600 * 10.0) / 10.0)
                : String.format("%s Miles Away",Math.round(miles* 10.0) / 10.0);

        //check if profile image available
        String userIcon = userModel.getImage();
        if(!TextUtils.isEmpty(userIcon)){
            //user has an profile image update
            //display it with glide
            Glide
                .with(context)
                .load(BASE_URL+"uploads/"+userIcon)
                .placeholder(R.drawable.loading_spinner)
                .into(holder.imgIcon);
        }


        holder.tvcompany_name.setText(userModel.getCompany_name());
        holder.tvcompany_distance.setText(distance);
        holder.cardTowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", userModelList.get(position).getPhone_number(), null));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    class TowingViewHolder extends RecyclerView.ViewHolder{
        TextView tvcompany_name,tvcompany_distance;
        CardView cardTowing;
        ImageView imgIcon;

        public TowingViewHolder(View itemView){
            super(itemView);

            tvcompany_name = itemView.findViewById(R.id.tvcompany_name);
            tvcompany_distance = itemView.findViewById(R.id.tvcompany_distance);
            cardTowing = itemView.findViewById(R.id.cardTowing);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }
}