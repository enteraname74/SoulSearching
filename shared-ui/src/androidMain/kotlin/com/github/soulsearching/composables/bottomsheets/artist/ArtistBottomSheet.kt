package com.github.soulsearching.composables.bottomsheets.artist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.ArtistState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ArtistBottomSheet(
    artistState: ArtistState,
    onArtistEvent: (ArtistEvent) -> Unit,
    artistModalSheetState: SheetState,
    navigateToModifyArtist: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            onArtistEvent(
                ArtistEvent.BottomSheet(
                    isShown = false
                )
            )
        },
        sheetState = artistModalSheetState,
        dragHandle = {}
    ) {
        ArtistBottomSheetMenu(
            modifyAction = {
                coroutineScope.launch { artistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!artistModalSheetState.isVisible) {
                            onArtistEvent(
                                ArtistEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                            navigateToModifyArtist(artistState.selectedArtist.artist.artistId.toString())
                        }
                    }
            },
            deleteAction = {
                onArtistEvent(ArtistEvent.DeleteDialog(isShown = true))
            },
            quickAccessAction = {
                onArtistEvent(ArtistEvent.UpdateQuickAccessState)
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
            },
            isInQuickAccess = artistState.selectedArtist.artist.isInQuickAccess
        )
    }
}