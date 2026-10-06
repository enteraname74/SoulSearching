package com.github.enteraname74.soulsearching.composables.bottomsheets.collection

import kotlin.uuid.Uuid

interface CollectionBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyCollection: (collectionId: Uuid) -> Unit
    val toAddToPlaylists: (musicIds: List<Uuid>) -> Unit
}
