package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import com.github.enteraname74.domain.model.Artist

sealed interface MultipleArtistsChoiceMode {
    data object InitialFetch : MultipleArtistsChoiceMode
    data class NewSongs(
        val multipleArtists: List<Artist>
    ) : MultipleArtistsChoiceMode

    data object GeneralCheck : MultipleArtistsChoiceMode
}