package com.github.cwramirezg.micredito.core.domain.models

abstract class BaseEntity {
    abstract val id: String
    abstract val createdAt: Long
    abstract val updatedAt: Long
}
