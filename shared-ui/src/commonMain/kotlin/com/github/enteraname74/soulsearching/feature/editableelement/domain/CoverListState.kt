package com.github.enteraname74.soulsearching.feature.editableelement.domain

sealed interface CoverListState {
    data object Loading: CoverListState
    data class Data(
        val covers: List<ByteArray>,
    ): CoverListState
}