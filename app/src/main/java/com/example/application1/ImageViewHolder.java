package com.example.application1;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageViewHolder extends RecyclerView.ViewHolder {
    private Context context;

    public ImageView imageViewGalleryImage;
    public TextView textViewGalleryUsername;

    public ImageViewHolder(@NonNull View itemView, Context context) {
        super(itemView);

        this.context = context;

        imageViewGalleryImage = itemView.findViewById(R.id.imageViewGalleryImage);
        textViewGalleryUsername = itemView.findViewById(R.id.textViewGalleryUsername);
    }

    public void setFull_name(String full_name) {
        textViewGalleryUsername.setText(full_name);
    }

    public void setImage_url(String image_url) {
        Glide.with(context).load(image_url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewGalleryImage);
    }

    public void setTimestamo(String timestamo) {

    }

    public void setUser_id(String user_id) {

    }
}
