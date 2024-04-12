package com.github.enteraname74.domain.util

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.ArtworkFactory
import java.io.File

/**
 * Utility class for updating a music file information.
 */
class MusicFileUpdater {
    fun updateMusic(music: Music, cover: ImageBitmap?) {
        try {
            val musicFile = File(music.path)

            println("Got music file: ${musicFile.isFile} ${musicFile.path}")

            val audioFile = AudioFileIO.read(musicFile)

            val tag = audioFile.tag

            println("NAME: ${tag.getFirst(FieldKey.TITLE)}")
            println("ALBUM: ${tag.getFirst(FieldKey.ALBUM)}")
            println("ARTIST: ${tag.getFirst(FieldKey.ARTIST)}")

            tag.setField(FieldKey.TITLE, music.name)
            tag.setField(FieldKey.ALBUM, music.album)
            tag.setField(FieldKey.ARTIST, music.artist)

            println("Cover: $cover")
            cover?.let { currentArtwork ->
                try {
                    val artwork = ArtworkFactory.getNew()
                    println("Got artwork! $artwork")
                    println("Bytes of artwork: ${currentArtwork.toBytes()}")
                    tag.deleteArtworkField();
                    artwork.binaryData = currentArtwork.toBytes()
                    tag.setField(artwork)
                } catch (e: Exception) {
                    println("MUSIC FILE UPDATER: Exception while writing cover: ${e.localizedMessage}")
                }
            }

            println("New name: ${tag.getFirst(FieldKey.TITLE)}")

            audioFile.commit()

        } catch (e: Exception) {
            println("MUSIC FILE UPDATER: Exception while writing to music file: ${e.localizedMessage}")
        }
    }
}

/**
 * Converts an ImageBitmap to a ByteArray representation of it.
 */
expect fun ImageBitmap?.toBytes(): ByteArray