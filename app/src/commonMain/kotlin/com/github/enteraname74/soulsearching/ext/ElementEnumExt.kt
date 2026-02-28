package com.github.enteraname74.soulsearching.ext

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_album
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_album_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_folder
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_folder_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_music_note_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_queue_music
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_quick_access_filled
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import org.jetbrains.compose.resources.DrawableResource

fun ElementEnum.navigationFilledIcon(): DrawableResource =
    when (this) {
        ElementEnum.QUICK_ACCESS -> CoreRes.drawable.ic_quick_access_filled
        ElementEnum.PLAYLISTS -> CoreRes.drawable.ic_queue_music
        ElementEnum.ALBUMS -> CoreRes.drawable.ic_album_filled
        ElementEnum.ARTISTS -> CoreRes.drawable.ic_person_filled
        ElementEnum.MUSICS -> CoreRes.drawable.ic_music_note_filled
        ElementEnum.FOLDERS -> CoreRes.drawable.ic_folder_filled
    }

// TODO ICONS: Find a way to have outlined icons of missing ones
fun ElementEnum.navigationOutlinedIcon(): DrawableResource =
    when (this) {
        ElementEnum.QUICK_ACCESS -> CoreRes.drawable.ic_quick_access_filled
        ElementEnum.PLAYLISTS -> CoreRes.drawable.ic_queue_music
        ElementEnum.ALBUMS -> CoreRes.drawable.ic_album
        ElementEnum.ARTISTS -> CoreRes.drawable.ic_person
        ElementEnum.MUSICS -> CoreRes.drawable.ic_music_note_filled
        ElementEnum.FOLDERS -> CoreRes.drawable.ic_folder
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