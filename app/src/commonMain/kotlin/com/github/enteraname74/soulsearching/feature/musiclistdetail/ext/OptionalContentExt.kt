package com.github.enteraname74.soulsearching.feature.musiclistdetail.ext

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.musiclistdetail.MusicListDetailState
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.ArtistAlbums

@Composable
fun MusicListDetailState.Data.OptionalContent.Content(
    multiSelectionState: MultiSelectionState,
) {
    when (this) {
        is MusicListDetailState.Data.OptionalContent.Albums -> {
            ArtistAlbums(
                spec = this,
                multiSelectionState = multiSelectionState,
            )
        }
    }
}