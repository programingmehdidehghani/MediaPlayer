package com.example.mediaplayersample.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mediaplayersample.R

object ImageLoader {

    fun loadImage(
        view: ImageView,
        url: String,
        placeholder: Int = R.drawable.baseline_assistant_24
    ) {
        Glide.with(view)
            .asBitmap()
            .load(url)
            .placeholder(placeholder)
            .override(128,128)
            .error(placeholder)
            .fallback(placeholder)
            .into(view)
    }
}