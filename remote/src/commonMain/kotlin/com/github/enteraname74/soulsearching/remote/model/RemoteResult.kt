package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.SoulResult

sealed interface RemoteResult<T> {
    data class Success<T>(val result: T) : RemoteResult<T> {
        override fun toSoulResult(): SoulResult<T> =
            SoulResult.Success(result)
        override fun <R> toSoulResult(mapData: (T) -> R): SoulResult<R> =
            SoulResult.Success(mapData(result))
        override fun toSimpleResult(): SoulResult<Unit> =
            SoulResult.Success(Unit)
    }
    data class Error<T>(val message: String?) : RemoteResult<T> {
        override fun toSoulResult(): SoulResult<T> =
            SoulResult.Error(message)

        override fun <R> toSoulResult(mapData: (T) -> R): SoulResult<R> =
            SoulResult.Error(message)

        override fun toSimpleResult(): SoulResult<Unit> =
            SoulResult.Error(message)
    }

    fun toSoulResult(): SoulResult<T>
    fun <R>toSoulResult(
        mapData: (T) -> R
    ): SoulResult<R>
    fun toSimpleResult(): SoulResult<Unit>
}