package com.github.enteraname74.domain.model

import java.util.UUID

/**
 * Represent the cover of a song, album, artist or playlist.
 */
data class ImageCover(
    val id : Long = 0L,
    val coverId : UUID = UUID.randomUUID(),
    val cover : ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageCover

        if (id != other.id) return false
        if (coverId != other.coverId) return false
        if (cover != null) {
            if (other.cover == null) return false
            if (!cover.contentEquals(other.cover)) return false
        } else if (other.cover != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + coverId.hashCode()
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        return result
    }
}