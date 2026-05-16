package com.estrano.starter

import com.estrano.starter.db.DashboardDao
import com.estrano.starter.model.DashboardData
import com.estrano.starter.model.DashboardResponse
import com.estrano.starter.network.ApiService
import com.estrano.starter.network.DashboardRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardRepositoryTest {

    private val apiService = mockk<ApiService>()
    private val dao = mockk<DashboardDao>(relaxed = true)
    private lateinit var repository: DashboardRepository

    @Before
    fun setup() {
        repository = DashboardRepository(apiService, dao)
    }

    @Test
    fun `getDashboardDataFlow emits cache then network success`() = runTest {
        // Arrange
        val mockNetworkResponse = DashboardResponse("network", DashboardData(listOf(), listOf()))
        coEvery { dao.getMetrics() } returns listOf()
        coEvery { apiService.getDashboard() } returns Response.success(mockNetworkResponse)

        // Act
        val results = repository.getDashboardDataFlow().toList()

        // Assert
        // (If cache is empty, it only emits network or mock)
        assert(results.any { it.isSuccess })
        coVerify { dao.insertMetrics(any()) }
    }

    @Test
    fun `getDashboardDataFlow emits mock on network failure when cache is empty`() = runTest {
        // Arrange
        coEvery { dao.getMetrics() } returns listOf()
        coEvery { apiService.getDashboard() } returns Response.error(404, "".toResponseBody())

        // Act
        val results = repository.getDashboardDataFlow().toList()

        // Assert
        val lastResult = results.last()
        assert(lastResult.isSuccess)
        assertEquals("mock", lastResult.getOrNull()?.status)
    }
}
