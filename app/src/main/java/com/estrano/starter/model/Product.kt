package com.estrano.starter.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String,
    @SerializedName("icon_res") val iconRes: Int,
    @SerializedName("rating") val rating: Float = 5.0f,
    @SerializedName("reviews") val reviews: Int = 120
)
