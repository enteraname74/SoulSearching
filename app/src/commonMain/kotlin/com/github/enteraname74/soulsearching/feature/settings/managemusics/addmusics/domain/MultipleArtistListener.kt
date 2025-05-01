package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MultipleArtistListener {
    private val _state: MutableStateFlow<MultipleArtistHandlingStep> = MutableStateFlow(MultipleArtistHandlingStep.Idle)
    val state: StateFlow<MultipleArtistHandlingStep> = _state.asStateFlow()

    fun consumeStep() {
        _state.value = MultipleArtistHandlingStep.Idle
    }

    fun toStep(step: MultipleArtistHandlingStep) {
        _state.value = step
    }
}

enum class MultipleArtistHandlingStep {
    Idle,
    UserChoice,
    SongsSaved,
}