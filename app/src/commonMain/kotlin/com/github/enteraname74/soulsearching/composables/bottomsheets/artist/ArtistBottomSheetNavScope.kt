package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import java.util.UUID

interface ArtistBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toModifyArtist: (artistId: UUID) -> Unit
}