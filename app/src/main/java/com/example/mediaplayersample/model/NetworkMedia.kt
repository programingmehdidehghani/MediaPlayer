package com.example.mediaplayersample.model

import com.google.gson.annotations.SerializedName

data class NetworkMedia(
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String
)
