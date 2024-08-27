package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey

abstract class PlaylistDetailScreen(
    id: String,
): Screen {
    final override val key: ScreenKey = "DETAIL-$id"
}