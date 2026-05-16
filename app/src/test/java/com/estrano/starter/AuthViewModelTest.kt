package com.estrano.starter

import com.estrano.core.session.SessionManager
import com.estrano.starter.viewmodel.AuthViewModel
import com.estrano.starter.network.AuthRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val sessionManager = mockk<SessionManager>(relaxed = true)
    private val authRepository = mockk<AuthRepository>(relaxed = true)

    @Before
    fun setup() {
        viewModel = AuthViewModel(sessionManager, authRepository)
    }

    @Test
    fun `loginWithKey with blank input emits error state`() = runTest {
        viewModel.loginWithKey("")
        val state = viewModel.authState.value
        assertNotNull(state)
        assertFalse(state!!.isSuccess)
        assertEquals("License key cannot be empty", state.error)
    }

    @Test
    fun `loginWithKey with invalid key returns error when repository rejects`() = runTest {
        val key = "bad-key"
        coEvery { authRepository.verifyLicense(key) } returns 
            Result.failure(Exception("Unauthorized: Invalid License Key"))

        viewModel.loginWithKey(key)
        
        val state = viewModel.authState.value
        assertNotNull(state)
        assertFalse(state!!.isSuccess)
        assertEquals("Unauthorized: Invalid License Key", state.error)
    }

    @Test
    fun `loginWithKey with valid ESTRANO key saves user and emits success`() = runTest {
        val key = "ESTRANO-1234-5678"
        coEvery { authRepository.verifyLicense(key) } returns 
            Result.success(AuthRepository.AuthResponse(true, "Operator", "$key@estrano.sys"))

        viewModel.loginWithKey(key)
        
        verify { sessionManager.saveUser(name = "Operator", email = "$key@estrano.sys") }
        
        val state = viewModel.authState.value
        assertTrue(state!!.isSuccess)
        assertEquals("Operator", state.name)
    }

    @Test
    fun `loginWithKey with email format saves user and emits success`() = runTest {
        val email = "admin@estrano.studio"
        coEvery { authRepository.verifyLicense(email) } returns 
            Result.success(AuthRepository.AuthResponse(true, "Admin", email))

        viewModel.loginWithKey(email)
        
        verify { sessionManager.saveUser(name = "Admin", email = email) }
        
        val state = viewModel.authState.value
        assertTrue(state!!.isSuccess)
        assertEquals("Admin", state.name)
    }
}
