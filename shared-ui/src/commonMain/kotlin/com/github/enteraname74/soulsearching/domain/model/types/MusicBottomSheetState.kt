package com.github.enteraname74.soulsearching.domain.model.types

/**
 * Used for defining the origin of a music.
 * It is primarily used to define what to show on a bottom sheet.
 */
enum class MusicBottomSheetState {
    NORMAL,
    PLAYLIST,
    ALBUM_OR_ARTIST,
    PLAYER
}