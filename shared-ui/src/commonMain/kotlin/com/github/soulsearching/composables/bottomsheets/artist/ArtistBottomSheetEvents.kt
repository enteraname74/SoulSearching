package com.github.soulsearching.composables.bottomsheets.artist

import androidx.compose.runtime.Composable
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.ArtistState

@Composable
expect fun ArtistBottomSheetEvents(
    artistState: ArtistState,
    onArtistEvent: (ArtistEvent) -> Unit,
    navigateToModifyArtist: (String) -> Unit
)