package com.github.enteraname74.soulsearching.ext


import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailPage
import com.github.enteraname74.soulsearching.navigation.Navigator

fun Navigator.isComingFromPlaylistDetails(): Boolean =
    currentRoute is PlaylistDetailPage

fun Navigator.isPreviousScreenAPlaylistDetails(): Boolean =
    stack.getOrNull(stack.lastIndex - 1) is PlaylistDetailPage