package com.estrano.starter.network

import com.estrano.starter.model.DashboardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @GET("dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>

    @POST("auth/verify")
    suspend fun verifyLicense(@Body request: LicenseRequest): Response<AuthResponseDTO>
}

data class LicenseRequest(val licenseKey: String)
data class AuthResponseDTO(
    val success: Boolean,
    val name: String,
    val email: String,
    val message: String? = null
)
