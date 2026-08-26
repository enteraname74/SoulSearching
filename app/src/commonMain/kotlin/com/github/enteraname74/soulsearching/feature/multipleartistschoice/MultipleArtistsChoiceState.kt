package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import com.github.enteraname74.domain.model.Artist

sealed interface MultipleArtistChoiceState {
    data object Loading : MultipleArtistChoiceState
    data class NoMultipleArtists(
        val navigateBack: () -> Unit,
    ) : MultipleArtistChoiceState

    data class UserAction(
        val artists: List<ArtistChoice>,
        val onSaveSelection: () -> Unit,
        val navigateBack: (() -> Unit)?,
        val onToggleAll: (Boolean) -> Unit,
        val onToggleArtistChoice: (ArtistChoice) -> Unit,
    ) : MultipleArtistChoiceState {
        val toggleAllState: Boolean = artists.all { it.isSelected }
    }
}

data class ArtistChoice(
    val artist: Artist,
    val isSelected: Boolean = true,
)
