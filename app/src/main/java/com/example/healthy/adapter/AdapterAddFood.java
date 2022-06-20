package com.example.healthy.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthy.R;
import com.example.healthy.databinding.ItemFoodBinding;
import com.example.healthy.model.Food;

import java.util.List;

public class AdapterAddFood extends RecyclerView.Adapter<AdapterAddFood.ViewHolder> {
    List<Food> list;

    public AdapterAddFood(List<Food> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterAddFood.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemFoodBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_food, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAddFood.ViewHolder holder, int position) {
        holder.binding.tvName.setText(list.get(position).getName());
        holder.binding.tvCalo.setText(list.get(position).getCalories().toString() +" kcal");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateAdapter(List<Food> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemFoodBinding binding;

        public ViewHolder(@NonNull ItemFoodBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
