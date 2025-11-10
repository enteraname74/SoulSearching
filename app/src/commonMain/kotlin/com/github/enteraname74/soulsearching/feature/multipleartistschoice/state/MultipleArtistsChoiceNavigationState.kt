package com.github.enteraname74.soulsearching.feature.multipleartistschoice.state

sealed interface MultipleArtistsChoiceNavigationState {
    data object Idle: MultipleArtistsChoiceNavigationState
    data object Quit: MultipleArtistsChoiceNavigationState
}