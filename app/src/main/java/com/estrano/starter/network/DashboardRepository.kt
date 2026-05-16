package com.estrano.starter.network

import com.estrano.starter.db.DashboardDao
import com.estrano.starter.db.MetricEntity
import com.estrano.starter.db.TimelineEntity
import com.estrano.starter.model.DashboardData
import com.estrano.starter.model.DashboardResponse
import com.estrano.starter.model.MetricItem
import com.estrano.starter.model.TimelineItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val apiService: ApiService,
    private val dao: DashboardDao
) {
    /**
     * OFFLINE-FIRST FLOW:
     * 1. Emit data from local DB cache immediately.
     * 2. Fetch fresh data from network.
     * 3. Update DB cache with fresh data.
     * 4. Emit updated data from network.
     */
    fun getDashboardDataFlow(): Flow<Result<DashboardResponse>> = flow {
        // Emit cache first
        val cachedMetrics = dao.getMetrics().map { it.toDomain() }
        val cachedTimeline = dao.getTimeline().map { it.toDomain() }
        
        if (cachedMetrics.isNotEmpty()) {
            emit(Result.success(DashboardResponse("cache", DashboardData(cachedMetrics, cachedTimeline))))
        }

        // Fetch from network
        try {
            val response = apiService.getDashboard()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                
                // Update cache
                dao.clearMetrics()
                dao.clearTimeline()
                dao.insertMetrics(data.data.metrics.map { it.toEntity() })
                dao.insertTimeline(data.data.timeline.map { it.toEntity() })
                
                emit(Result.success(data))
            } else {
                // If network fails but we had cache, we've already emitted cache
                // If no cache, emit mock as ultimate fallback
                if (cachedMetrics.isEmpty()) {
                    emit(Result.success(getMockDashboardData()))
                }
            }
        } catch (e: Exception) {
            if (cachedMetrics.isEmpty()) {
                emit(Result.success(getMockDashboardData()))
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun getMockDashboardData(): DashboardResponse {
        val metrics = listOf(
            MetricItem("Total Revenue", "$124,500", "+12% from last month", 85),
            MetricItem("Active Users", "12,402", "Live Now", 65),
            MetricItem("Server Load", "24%", "Stable", 24)
        )
        val timeline = listOf(
            TimelineItem("01", "Security Audit Passed", "Aether Integrity Shield v4.0 verified.", "2m ago"),
            TimelineItem("02", "Deployment Success", "V0.9.4 production build is live.", "1h ago"),
            TimelineItem("03", "System Maintenance", "Database optimization completed.", "5h ago")
        )
        return DashboardResponse("mock", DashboardData(metrics, timeline))
    }

    // Mapper extensions
    private fun MetricEntity.toDomain() = MetricItem(title, value, subtitle, progress)
    private fun MetricItem.toEntity() = MetricEntity(title, value, subtitle, progress)
    private fun TimelineEntity.toDomain() = TimelineItem(step, title, description, timestamp)
    private fun TimelineItem.toEntity() = TimelineEntity(title, step, description, timestamp)
}
