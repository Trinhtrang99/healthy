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

import com.example.healthy.ActivityDetailEvent;
import com.example.healthy.R;
import com.example.healthy.databinding.LineEventBinding;
import com.example.healthy.model.Event;

import java.util.List;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.ViewHolder> {
    List<Event> list;
    Context context;

    public AdapterEvent(List<Event> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterEvent.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LineEventBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.line_event, parent, false);
        return new ViewHolder(binding);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AdapterEvent.ViewHolder holder, int position) {
        holder.binding.img.setImageDrawable(context.getDrawable(list.get(position).getImg()));
        holder.binding.tvName.setText(list.get(position).getName());
        holder.binding.img.setOnClickListener(view -> {
            Intent i = new Intent(context, ActivityDetailEvent.class);
            i.putExtra("img", list.get(position).getImg());
            i.putExtra("playId", list.get(position).getPlayId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LineEventBinding binding;

        public ViewHolder(@NonNull LineEventBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
