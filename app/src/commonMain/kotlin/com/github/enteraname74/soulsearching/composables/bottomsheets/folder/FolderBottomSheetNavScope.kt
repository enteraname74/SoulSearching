package com.github.enteraname74.soulsearching.composables.bottomsheets.folder

import kotlin.uuid.Uuid

interface FolderBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toAddToPlaylists: (musicIds: List<Uuid>) -> Unit
}
