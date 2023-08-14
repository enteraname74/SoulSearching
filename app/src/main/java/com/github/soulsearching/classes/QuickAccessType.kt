package com.github.soulsearching.classes

object QuickAccessType {
    const val MUSIC = 0
    const val PLAYLIST = 1
    const val ALBUM = 2
    const val ARTIST = 3

    fun getQuickAccessType(type: Int): Int {
        return if (type <= 3) {
            type
        } else {
            0
        }
    }
}