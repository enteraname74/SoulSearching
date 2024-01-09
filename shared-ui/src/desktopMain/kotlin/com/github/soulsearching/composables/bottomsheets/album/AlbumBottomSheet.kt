package com.github.soulsearching.composables.bottomsheets.album

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AlbumBottomSheet(
    albumState: AlbumState,
    onAlbumEvent: (AlbumEvent) -> Unit,
    albumModalSheetState: SheetState,
    navigateToModifyAlbum: (String) -> Unit
) {

}