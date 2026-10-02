package com.github.enteraname74.soulsearching.composables.bottomsheets.collection

import kotlin.uuid.Uuid

interface CollectionBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toAddToPlaylists: (musicIds: List<Uuid>) -> Unit
}
