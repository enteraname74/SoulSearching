package com.github.enteraname74.domain.model

sealed interface SoulResult<T> {
    data class Success<T>(val data: T) : SoulResult<T> {
        override fun <R> map(mapData: (T) -> R): SoulResult<R> =
            Success(mapData(this.data))

        override fun toSimpleResult(): SoulResult<Unit> = Success(Unit)
    }

    data class Error<T>(val error: String?) : SoulResult<T> {
        override fun <R> map(mapData: (T) -> R): SoulResult<R> = Error(error)
        override fun toSimpleResult(): SoulResult<Unit> = Error(error)
    }

    fun <R> map(mapData: (T) -> R): SoulResult<R>
    fun toSimpleResult(): SoulResult<Unit>

    fun isError(): Boolean =
        this is Error

    companion object {
        fun ofSuccess(): Success<Unit> = Success(Unit)
    }
}