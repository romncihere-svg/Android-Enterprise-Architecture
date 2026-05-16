package com.estrano.starter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.estrano.starter.model.DashboardData
import com.estrano.starter.model.DashboardResponse
import com.estrano.starter.network.DashboardRepository
import com.estrano.starter.viewmodel.HomeViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository = mockk<DashboardRepository>()
    private lateinit var viewModel: HomeViewModel
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `loadDashboardData should update metrics when repository returns success`() {
        // Arrange
        val mockResponse = DashboardResponse("success", DashboardData(listOf(), listOf()))
        every { repository.getDashboardDataFlow() } returns flowOf(Result.success(mockResponse))
        
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isLoading.observeForever(observer)

        // Act
        viewModel.loadDashboardData()

        // Assert
        verify { observer.onChanged(true) }
        assert(viewModel.metrics.value != null)
        verify { observer.onChanged(false) }
    }
}
