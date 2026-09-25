package com.github.enteraname74.soulsearching.domain.model

sealed interface SoulResult<T> {
    data class Success<T>(val data: T) : SoulResult<T> {
        override fun <R> map(mapData: (T) -> R): SoulResult<R> =
            Success(mapData(this.data))

        override fun toSimpleResult(): SoulResult<Unit> = Success(Unit)
    }

    data class Error<T>(
        val error: String? = null,
        val throwable: Throwable? = null,
    ) : SoulResult<T> {
        constructor(throwable: Throwable) : this(
            error = throwable.message,
            throwable = throwable,
        )

        override fun <R> map(mapData: (T) -> R): SoulResult<R> = Error(error, throwable)
        override fun toSimpleResult(): SoulResult<Unit> = Error(error, throwable)
    }

    fun <R> map(mapData: (T) -> R): SoulResult<R>
    fun toSimpleResult(): SoulResult<Unit>

    fun isError(): Boolean =
        this is Error

    fun throwIfError() {
        (this as? Error)?.let {
            throw throwable ?: Exception(error.orEmpty())
        }
    }

    fun onFailure(block: Error<T>.() -> Unit): SoulResult<T> {
        if (this is Error) {
            block()
        }
        return this
    }

    suspend fun onSuccess(block: suspend Success<T>.() -> Unit): SoulResult<T> {
        if (this is Success) {
            block()
        }
        return this
    }

    fun getOrNull(): T? =
        when (this) {
            is Error<T> -> null
            is Success<T> -> data
        }

    fun getOrElse(block: () -> T): T =
        when (this) {
            is Error -> block()
            is Success -> data
        }

    companion object {
        fun ofSuccess(): Success<Unit> = Success(Unit)

        suspend fun <T> runCatching(block: suspend () -> T): SoulResult<T> =
            try {
                Success(block())
            } catch (e: Exception) {
                Error(e)
            }
    }
}
