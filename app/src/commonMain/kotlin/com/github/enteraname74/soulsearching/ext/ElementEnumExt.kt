package com.github.enteraname74.soulsearching.ext

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_album
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_album_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_folder
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_folder_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_music_note_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_library_music
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_queue_music
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_quick_access_filled
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import org.jetbrains.compose.resources.DrawableResource

fun ElementEnum.filledIcon(): DrawableResource =
    when (this) {
        ElementEnum.QUICK_ACCESS -> CoreRes.drawable.ic_quick_access_filled
        ElementEnum.PLAYLISTS -> CoreRes.drawable.ic_queue_music
        ElementEnum.COLLECTIONS -> CoreRes.drawable.ic_library_music
        ElementEnum.ALBUMS -> CoreRes.drawable.ic_album_filled
        ElementEnum.ARTISTS -> CoreRes.drawable.ic_person_filled
        ElementEnum.MUSICS -> CoreRes.drawable.ic_music_note_filled
        ElementEnum.FOLDERS -> CoreRes.drawable.ic_folder_filled
    }

// TODO ICONS: Find a way to have outlined icons of missing ones
fun ElementEnum.outlinedIcon(): DrawableResource =
    when (this) {
        ElementEnum.QUICK_ACCESS -> CoreRes.drawable.ic_quick_access_filled
        ElementEnum.PLAYLISTS -> CoreRes.drawable.ic_queue_music
        ElementEnum.COLLECTIONS -> CoreRes.drawable.ic_library_music
        ElementEnum.ALBUMS -> CoreRes.drawable.ic_album
        ElementEnum.ARTISTS -> CoreRes.drawable.ic_person
        ElementEnum.MUSICS -> CoreRes.drawable.ic_music_note_filled
        ElementEnum.FOLDERS -> CoreRes.drawable.ic_folder
    }

fun ElementEnum.text(): SoulTextSpec =
    when (this) {
        ElementEnum.QUICK_ACCESS -> CoreRes.string.quick_access.toTextSpec()
        ElementEnum.PLAYLISTS -> CoreRes.string.playlists.toTextSpec()
        ElementEnum.COLLECTIONS -> CoreRes.string.collections.toTextSpec()
        ElementEnum.ALBUMS -> CoreRes.string.albums.toTextSpec()
        ElementEnum.ARTISTS -> CoreRes.string.artists.toTextSpec()
        ElementEnum.MUSICS -> CoreRes.string.musics.toTextSpec()
        ElementEnum.FOLDERS -> CoreRes.string.folders.toTextSpec()
    }
