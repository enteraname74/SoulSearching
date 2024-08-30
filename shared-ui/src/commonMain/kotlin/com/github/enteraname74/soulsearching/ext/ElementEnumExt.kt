package com.github.enteraname74.soulsearching.ext

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum

fun ElementEnum.navigationIcon(): ImageVector =
    when (this) {
        ElementEnum.QUICK_ACCESS -> Icons.Rounded.DoubleArrow
        ElementEnum.PLAYLISTS -> Icons.AutoMirrored.Rounded.QueueMusic
        ElementEnum.ALBUMS -> Icons.Rounded.Album
        ElementEnum.ARTISTS -> Icons.Rounded.Person
        ElementEnum.MUSICS -> Icons.Rounded.MusicNote
        ElementEnum.FOLDERS -> Icons.Rounded.Folder
    }

fun ElementEnum.navigationTitle(): String =
    when (this) {
        ElementEnum.QUICK_ACCESS -> strings.quickAccess
        ElementEnum.PLAYLISTS -> strings.playlists
        ElementEnum.ALBUMS -> strings.albums
        ElementEnum.ARTISTS -> strings.artists
        ElementEnum.MUSICS -> strings.musics
        ElementEnum.FOLDERS -> strings.folders
    }