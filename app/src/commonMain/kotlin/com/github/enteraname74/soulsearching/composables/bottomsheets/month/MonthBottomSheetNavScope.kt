package com.github.enteraname74.soulsearching.composables.bottomsheets.month

import kotlin.uuid.Uuid

interface MonthBottomSheetNavScope {
    val navigateBack: () -> Unit
    val toAddToPlaylists: (musicIds: List<Uuid>) -> Unit
}
