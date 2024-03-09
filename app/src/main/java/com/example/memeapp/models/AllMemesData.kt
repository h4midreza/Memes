package com.example.memeapp.models


import com.google.gson.annotations.SerializedName

data class AllMemesData(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("success")
    val success: Boolean
)