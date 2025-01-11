package com.github.enteraname74.domain.util

import com.github.enteraname74.domain.model.SoulResult
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface FlowResult<T> {
    data class Success<T>(val data: T?): FlowResult<T>
    data class Loading<T>(val progress: Float?): FlowResult<T>
    data class Error<T>(val message: String?): FlowResult<T>

    companion object {
        fun <T>fromSoulResult(soulResult: SoulResult<T>) = when(soulResult) {
            is SoulResult.Error -> Error(message = soulResult.error)
            is SoulResult.Success -> Success(data = soulResult.data)
        }
    }
}

suspend fun <T, R>handleFlowResultOn(
    flow: MutableStateFlow<FlowResult<T>>,
    block: suspend ((Float?) -> Unit) -> SoulResult<R>,
): SoulResult<R> {
    flow.value = FlowResult.Loading(progress = 0f)
    return try {
        val result: SoulResult<R> = block { progress ->
            flow.value = FlowResult.Loading(progress = progress)
        }
        flow.value = if (result is SoulResult.Error) {
            FlowResult.Error(result.error)
        } else {
            FlowResult.Success(null)
        }
        result
    } catch (e: Exception) {
        flow.value = FlowResult.Error(e.message)
        SoulResult.Error(e.message)
    }
}