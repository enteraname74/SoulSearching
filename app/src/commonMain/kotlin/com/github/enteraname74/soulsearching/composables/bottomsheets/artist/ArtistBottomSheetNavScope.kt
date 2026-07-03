package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import kotlin.uuid.Uuid

interface ArtistBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyArtist: (artistId: Uuid) -> Unit
}
