package com.estrano.starter.di

import android.content.Context
import androidx.room.Room
import com.estrano.starter.db.AppDatabase
import com.estrano.starter.db.DashboardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "aether_database"
        ).build()
    }

    @Provides
    fun provideDashboardDao(database: AppDatabase): DashboardDao {
        return database.dashboardDao()
    }
}
