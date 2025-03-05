package com.github.enteraname74.soulsearching.ext

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailScreen

/**
 * Checks if we are not already on the same screen before navigating.
 */
fun Navigator.safePush(screen: Screen) {
    if (this.lastItem.key != screen.key) {
        this.push(screen)
    }
}

fun Navigator.isComingFromPlaylistDetails(): Boolean =
    this.lastItem is PlaylistDetailScreen

fun Navigator.isPreviousScreenAPlaylistDetails(): Boolean {
    val screens = this.items
    return screens.getOrNull(screens.lastIndex - 1) is PlaylistDetailScreen
}