package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import kotlin.uuid.Uuid

interface MusicBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyMusic: (musicId: Uuid) -> Unit
    val toAddToPlaylists: (musicIds: List<Uuid>) -> Unit
}
