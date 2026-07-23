package com.github.enteraname74.soulsearching.features.musicmanager.ext

import android.database.Cursor
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import java.io.File

private fun Cursor.getFilteredSafeString(index: Int): String =
    getString(index)?.trim().orEmpty()

fun Cursor.toMusic(): Music? =
    try {
        val albumArtist = this.getFilteredSafeString(6).takeIf { it.isNotBlank() }
        val artist = this.getFilteredSafeString(1)

        val artists: List<Artist> = buildList {
            if (albumArtist != null && albumArtist != artist) {
                add(Artist(artistName = albumArtist))
            }
            add(Artist(artistName = artist))
        }

        Music(
            name = this.getFilteredSafeString(0),
            album = Album(
                albumName = this.getFilteredSafeString(2),
                artist = artists.first(),
            ),
            artists = artists,
            duration = this.getLong(3),
            path = this.getFilteredSafeString(4),
            folder = File(this.getFilteredSafeString(4)).parent ?: "",
            cover = Cover.CoverFile(initialCoverPath = this.getFilteredSafeString(4)),
            albumPosition = this.getFilteredSafeString(5).toIntOrNull(),
        )
    } catch (e: Exception) {
        println("Cursor to Music -- Exception while fetching song on the device: $e")
        null
    }