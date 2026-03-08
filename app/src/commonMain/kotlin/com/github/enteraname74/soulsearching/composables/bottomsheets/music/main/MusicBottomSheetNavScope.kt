package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import java.util.UUID

interface MusicBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyMusic: (musicId: UUID) -> Unit
    val toAddToPlaylists: (musicIds: List<UUID>) -> Unit
}