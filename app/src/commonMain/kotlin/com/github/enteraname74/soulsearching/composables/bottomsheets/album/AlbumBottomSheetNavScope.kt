package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import java.util.UUID

interface AlbumBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyAlbum: (albumId: UUID) -> Unit
}