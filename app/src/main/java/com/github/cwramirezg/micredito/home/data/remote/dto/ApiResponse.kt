package com.github.cwramirezg.micredito.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: T?,
    @SerialName("message") val message: String?,
    @SerialName("error_code") val errorCode: String?
)
