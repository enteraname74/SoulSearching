package com.github.enteraname74.domain.model

sealed interface SoulResult {
    data class Success<T>(val result: T) : SoulResult
    data class Error(val error: String?) : SoulResult

    fun isError(): Boolean =
        this is Error
}