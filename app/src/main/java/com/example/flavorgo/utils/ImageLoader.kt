package com.example.flavorgo.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageLoader {
    fun loadImage(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }
}
