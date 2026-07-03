package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import kotlin.uuid.Uuid

interface PlaylistBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyPlaylist: (playlistId: Uuid) -> Unit
}
