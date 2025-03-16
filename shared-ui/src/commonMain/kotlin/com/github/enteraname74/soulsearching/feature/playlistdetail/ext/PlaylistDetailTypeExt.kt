package com.github.enteraname74.soulsearching.feature.playlistdetail.ext

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailType

@Composable
fun PlaylistDetailType.title(): String =
    when(this) {
        PlaylistDetailType.Playlist -> strings.playlistDetailTitle
        PlaylistDetailType.Album -> strings.albumDetailTitle
        PlaylistDetailType.Artist -> strings.artistDetailTitle
        PlaylistDetailType.Month -> strings.monthDetailTitle
        PlaylistDetailType.Folder -> strings.folderDetailTitle
    }