package com.github.soulsearching.composables.bottomsheets.album

import androidx.compose.runtime.Composable
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState

@Composable
expect fun AlbumBottomSheetEvents(
    albumState: AlbumState,
    onAlbumEvent: (AlbumEvent) -> Unit,
    navigateToModifyAlbum: (String) -> Unit
)