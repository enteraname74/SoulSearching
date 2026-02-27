package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import java.util.UUID

interface PlaylistBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyPlaylist: (playlistId: UUID) -> Unit
}