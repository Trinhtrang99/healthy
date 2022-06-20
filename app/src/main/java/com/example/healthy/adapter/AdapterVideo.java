package com.example.healthy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthy.ActivityPlayVideo;
import com.example.healthy.R;
import com.example.healthy.databinding.LineVideoBinding;
import com.example.healthy.network.response.Video;

import java.util.List;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.ViewHolder> {
    List<Video.abc> list;
    Context context;

    public AdapterVideo(List<Video.abc> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterVideo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LineVideoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.line_video, parent, false);
        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterVideo.ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getSnippet().getThumbnails().getHigh().getUrlImg()).into(holder.binding.img);
        holder.binding.tvNameVideo.setText(list.get(position).getSnippet().getTitle());
        holder.binding.getRoot().setOnClickListener(view -> {
            Intent i = new Intent(context, ActivityPlayVideo.class);
            i.putExtra("video", list.get(position).getSnippet().getResourceId().getVideoId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LineVideoBinding binding;

        public ViewHolder(@NonNull LineVideoBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
