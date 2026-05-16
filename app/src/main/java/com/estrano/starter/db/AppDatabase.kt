package com.estrano.starter.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MetricEntity::class, TimelineEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dashboardDao(): DashboardDao
}
