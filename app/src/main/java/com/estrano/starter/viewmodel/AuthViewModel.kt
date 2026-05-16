package com.estrano.starter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estrano.core.session.SessionManager
import com.estrano.starter.network.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState?>(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    fun loginWithKey(licenseKey: String) {
        if (licenseKey.isBlank()) {
            _authState.value = AuthState(false, error = "License key cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState(isSuccess = false, isLoading = true)
            
            val result = authRepository.verifyLicense(licenseKey)
            
            result.onSuccess { response ->
                sessionManager.saveUser(name = response.displayName, email = response.email)
                _authState.value = AuthState(isSuccess = true, name = response.displayName, email = response.email)
            }.onFailure { exception ->
                _authState.value = AuthState(isSuccess = false, error = exception.message)
            }
        }
    }

    fun handleError(message: String) {
        _authState.value = AuthState(isSuccess = false, error = message)
    }

    data class AuthState(
        val isSuccess: Boolean,
        val isLoading: Boolean = false,
        val name: String? = null,
        val email: String? = null,
        val error: String? = null
    )
}
