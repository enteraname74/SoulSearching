package com.github.soulsearching.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.model.MusicFetcher
import com.github.soulsearching.domain.model.SelectableMusicItem
import com.github.soulsearching.strings.strings
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jetbrains.skia.Image
import java.io.File
import java.util.UUID


/**
 * Class handling music fetching for desktop application.
 */
class MusicFetcherDesktopImpl(
    private val playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    folderRepository: FolderRepository
): MusicFetcher(
    musicRepository = musicRepository,
    albumRepository = albumRepository,
    artistRepository = artistRepository,
    musicAlbumRepository = musicAlbumRepository,
    musicArtistRepository = musicArtistRepository,
    albumArtistRepository = albumArtistRepository,
    imageCoverRepository = imageCoverRepository,
    folderRepository = folderRepository
) {

    /**
     * Tries to retrieves the cover of a music from its metadata.
     */
    private fun fetchMusicCoverFromMetadata(tag: Tag): ImageBitmap? {
        return try {
            Image.makeFromEncoded(tag.firstArtwork.binaryData).toComposeImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Extract mp3 files from current directory.
     */
    private suspend fun extractMusicsFromCurrentDirectory(directory: File) {
        val files = directory.listFiles() ?: return
        for (file in files) {
            if (file.isHidden || !file.canRead()) {
                continue
            }
            else if (file.name.endsWith(".mp3") || file.name.endsWith(".m4a")) {
                try {
                    val audioFile: AudioFile = AudioFileIO.read(file)
                    val tag: Tag = audioFile.tag

                    val musicToAdd = Music(
                        name = tag.getFirst(FieldKey.TITLE),
                        album = tag.getFirst(FieldKey.ALBUM),
                        artist = tag.getFirst(FieldKey.ARTIST),
                        duration = audioFile.audioHeader.trackLength.toLong(),
                        path = file.path,
                        folder = file.parent
                    )
                    val musicCover = fetchMusicCoverFromMetadata(tag)
                    addMusic(musicToAdd, musicCover)
                } catch (_: Exception) {
                    println("Failed to access information about the following file: ${file.path}")
                }
            }

            if (file.isDirectory) {
                extractMusicsFromCurrentDirectory(file)
            }

        }
    }

    override suspend fun fetchMusics(updateProgress: (Float) -> Unit, finishAction: () -> Unit) {
        val root = File("/home")
        extractMusicsFromCurrentDirectory(root)
        playlistRepository.insertPlaylist(
            Playlist(
                playlistId = UUID.randomUUID(),
                name = strings.favorite,
                isFavorite = true
            )
        )
        finishAction()
    }

    override fun fetchMusicsFromSelectedFolders(
        updateProgress: (Float) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): ArrayList<SelectableMusicItem> {
        return ArrayList()
    }
}