package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.enteraname74.soulsearching.composables.dialog.DeleteArtistDialog
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.domain.events.ArtistEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ArtistState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistBottomSheetEvents(
    artistState: ArtistState,
    onArtistEvent: (ArtistEvent) -> Unit,
    navigateToModifyArtist: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val artistModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    SoulBackHandler(artistModalSheetState.isVisible) {
        coroutineScope.launch { artistModalSheetState.hide() }
    }

    if (artistState.isDeleteDialogShown) {
        DeleteArtistDialog(
            onArtistEvent = onArtistEvent,
            confirmAction = {
                coroutineScope.launch { artistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!artistModalSheetState.isVisible) {
                            onArtistEvent(
                                ArtistEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                        }
                    }
            }
        )
    }

    if (artistState.isBottomSheetShown) {
        ArtistBottomSheet(
            onArtistEvent = onArtistEvent,
            artistModalSheetState = artistModalSheetState,
            navigateToModifyArtist = navigateToModifyArtist,
            artistState = artistState
        )
    }
}