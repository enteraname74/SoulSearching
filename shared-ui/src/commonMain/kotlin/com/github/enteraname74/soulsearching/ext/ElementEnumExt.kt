package com.github.enteraname74.soulsearching.ext

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum

fun ElementEnum.navigationFilledIcon(): ImageVector =
    when (this) {
        ElementEnum.QUICK_ACCESS -> Icons.Filled.DoubleArrow
        ElementEnum.PLAYLISTS -> Icons.AutoMirrored.Filled.QueueMusic
        ElementEnum.ALBUMS -> Icons.Filled.Album
        ElementEnum.ARTISTS -> Icons.Filled.Person
        ElementEnum.MUSICS -> Icons.Filled.MusicNote
        ElementEnum.FOLDERS -> Icons.Filled.Folder
    }

fun ElementEnum.navigationOutlinedIcon(): ImageVector =
    when (this) {
        ElementEnum.QUICK_ACCESS -> Icons.Outlined.DoubleArrow
        ElementEnum.PLAYLISTS -> Icons.AutoMirrored.Outlined.QueueMusic
        ElementEnum.ALBUMS -> Icons.Outlined.Album
        ElementEnum.ARTISTS -> Icons.Outlined.Person
        ElementEnum.MUSICS -> Icons.Outlined.MusicNote
        ElementEnum.FOLDERS -> Icons.Outlined.Folder
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