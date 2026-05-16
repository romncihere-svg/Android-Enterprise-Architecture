package com.estrano.starter.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: DashboardData
)

data class DashboardData(
    @SerializedName("metrics") val metrics: List<MetricItem>,
    @SerializedName("timeline") val timeline: List<TimelineItem>
)

data class MetricItem(
    @SerializedName("title") val title: String,
    @SerializedName("value") val value: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("progress") val progress: Int
)

data class TimelineItem(
    @SerializedName("step") val step: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("timestamp") val timestamp: String
)
