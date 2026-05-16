package com.estrano.starter.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DashboardDao {
    @Query("SELECT * FROM metrics")
    suspend fun getMetrics(): List<MetricEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetrics(metrics: List<MetricEntity>)

    @Query("SELECT * FROM timeline")
    suspend fun getTimeline(): List<TimelineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeline(timeline: List<TimelineEntity>)

    @Query("DELETE FROM metrics")
    suspend fun clearMetrics()

    @Query("DELETE FROM timeline")
    suspend fun clearTimeline()
}
