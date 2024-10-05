package com.github.enteraname74.domain.util

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.ArtworkFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Utility class for updating a music file information.
 */
class MusicFileUpdater : KoinComponent {

    private val settings by inject<SoulSearchingSettings>()
    private val coverFileManager by inject<CoverFileManager>()

    /**
     * Updates the file metadata of a music.
     * Does nothing if the user has not accepted files to be modified.
     */
    suspend fun updateMusic(music: Music) {
        if (!settings.get(SoulSearchingSettingsKeys.IS_MUSIC_FILE_MODIFICATION_ON)) return
        try {
            val musicFile = File(music.path)

            val audioFile = AudioFileIO.read(musicFile)

            val tag = audioFile.tag

            tag.setField(FieldKey.TITLE, music.name)
            tag.setField(FieldKey.ALBUM, music.album)
            tag.setField(FieldKey.ARTIST, music.artist)

            (music.cover as? Cover.FileCover)?.fileCoverId?.let { coverId ->
                val coverData: ByteArray? = coverFileManager.getCoverData(coverId = coverId)

                coverData?.let { currentArtwork ->
                    try {
                        val artwork = ArtworkFactory.getNew()
                        tag.deleteArtworkField()
                        artwork.binaryData = currentArtwork
                        tag.setField(artwork)
                    } catch (e: Exception) {
                        println("MUSIC FILE UPDATER: Exception while writing cover: ${e.localizedMessage}")
                    }
                }
            }

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