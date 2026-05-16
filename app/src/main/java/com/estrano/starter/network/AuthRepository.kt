package com.estrano.starter.network

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService // We'll assume the API service handles auth calls
) {
    /**
     * Performs a real network request to verify the license key against the backend.
     */
    suspend fun verifyLicense(licenseKey: String): Result<AuthResponse> {
        return try {
            val response = apiService.verifyLicense(LicenseRequest(licenseKey))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(AuthResponse(dto.success, dto.name, dto.email))
            } else {
                Result.failure(Exception("Unauthorized: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    data class AuthResponse(
        val success: Boolean,
        val displayName: String,
        val email: String
    )
}
