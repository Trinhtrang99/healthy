package com.example.healthy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthy.R;
import com.example.healthy.calories.CaloriesActivity;
import com.example.healthy.databinding.ItemInfoBinding;
import com.example.healthy.heart_rate.HeartRate;
import com.example.healthy.model.Information;

import java.util.List;

public class AdapterInfo extends RecyclerView.Adapter<AdapterInfo.ViewHolder> {
    List<Information> list;
    Context context;

    public AdapterInfo(List<Information> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterInfo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInfoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_info, parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AdapterInfo.ViewHolder holder, int position) {
        holder.binding.imgInf.setImageDrawable(context.getDrawable(list.get(position).getDrawable()));
        holder.binding.txtIndex.setText(list.get(position).getIndex());
        holder.binding.txtIndex.setTextColor(context.getResources().getColor(list.get(position).getColor()));
        holder.binding.txtTt.setTextColor(context.getResources().getColor(list.get(position).getColor()));
        holder.binding.txtTt.setText(list.get(position).getName());
        holder.binding.getRoot().setOnClickListener(view -> {
            if (position == 0) {
                context.startActivity(new Intent(context, HeartRate.class));
            } else if (position == 5) {
                context.startActivity(new Intent(context, CaloriesActivity.class));
            }

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemInfoBinding binding;

        public ViewHolder(@NonNull ItemInfoBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
    public void updateData(    List<Information> list){
        this.list = list;
        notifyDataSetChanged();
    }

}
