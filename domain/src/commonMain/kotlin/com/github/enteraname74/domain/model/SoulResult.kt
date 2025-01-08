package com.github.enteraname74.domain.model

sealed interface SoulResult<T> {
    data class Success<T>(val result: T) : SoulResult<T> {
        override fun toSimpleResult(): SoulResult<Unit> = Success(Unit)
    }
    data class Error<T>(val error: String?) : SoulResult<T> {
        override fun toSimpleResult(): SoulResult<Unit> = Error(error)
    }

    fun toSimpleResult(): SoulResult<Unit>

    fun isError(): Boolean =
        this is Error

    companion object {
        fun ofSuccess(): Success<Unit> = Success(Unit)
    }
}