package com.example.healthy.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthy.R;
import com.example.healthy.databinding.ItemWaterBinding;

import java.util.List;

public class AdaterWater extends RecyclerView.Adapter<AdaterWater.ViewHolder> {
    List<String> list;

    public AdaterWater(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AdaterWater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWaterBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_water, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaterWater.ViewHolder holder, int position) {
        holder.binding.time.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateAdapter(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemWaterBinding binding;

        public ViewHolder(@NonNull ItemWaterBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
