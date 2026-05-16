package com.estrano.starter.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metrics")
data class MetricEntity(
    @PrimaryKey val title: String,
    val value: String,
    val subtitle: String,
    val progress: Int
)

@Entity(tableName = "timeline")
data class TimelineEntity(
    @PrimaryKey val title: String,
    val step: String,
    val description: String,
    val timestamp: String
)
