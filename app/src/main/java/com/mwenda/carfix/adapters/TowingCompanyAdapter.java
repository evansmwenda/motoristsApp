package com.mwenda.carfix.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mwenda.carfix.R;
import com.mwenda.carfix.models.User;

import java.util.List;

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

        holder.tvcompany_name.setText(userModel.getCompany_name());
        holder.tvcompany_phone.setText(userModel.getPhone_number());
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
        TextView tvcompany_name,tvcompany_phone;
        CardView cardTowing;

        public TowingViewHolder(View itemView){
            super(itemView);

            tvcompany_name = itemView.findViewById(R.id.tvcompany_name);
            tvcompany_phone = itemView.findViewById(R.id.tvcompany_phone);
            cardTowing = itemView.findViewById(R.id.cardTowing);
        }
    }
}