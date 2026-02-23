package com.github.enteraname74.soulsearching.domain.model.types

import kotlinx.serialization.Serializable

/**
 * Used for defining the origin of a music.
 * It is primarily used to define what to show on a bottom sheet.
 */
@Serializable
enum class MusicBottomSheetMode {
    NORMAL,
    PLAYLIST,
    ALBUM_OR_ARTIST,
}