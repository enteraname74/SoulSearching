package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import kotlin.uuid.Uuid

interface AlbumBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyAlbum: (albumId: Uuid) -> Unit
    val toAddToPlaylists: (musicIds: List<Uuid>) -> Unit
}
