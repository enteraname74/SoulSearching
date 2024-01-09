package com.github.soulsearching.composables.bottomsheets.artist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.ArtistState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ArtistBottomSheet(
    artistState: ArtistState,
    onArtistEvent: (ArtistEvent) -> Unit,
    artistModalSheetState: SheetState,
    navigateToModifyArtist: (String) -> Unit
) {

}