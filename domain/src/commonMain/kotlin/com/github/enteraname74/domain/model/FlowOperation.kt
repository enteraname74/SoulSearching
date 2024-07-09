package com.github.enteraname74.domain.model

sealed interface FlowOperation {
    data object Loading: FlowOperation
    data class Error(val throwable: Throwable): FlowOperation
    data object Success: FlowOperation
}