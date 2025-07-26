package com.github.cwramirezg.micredito.core.domain.models

data class ErrorEntity(
    val message: String,
    val code: Int? = null,
    val type: ErrorType = ErrorType.UNKNOWN
)

enum class ErrorType {
    NETWORK,
    VALIDATION,
    AUTHENTICATION,
    SERVER,
    UNKNOWN
}
