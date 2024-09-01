package com.github.enteraname74.soulsearching.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.album.UpdateAlbumCoverUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistFromNameUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistCoverUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertFolderUseCase
import com.github.enteraname74.domain.usecase.imagecover.GetCoverOfElementUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.music.IsMusicAlreadySavedUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicalbum.UpsertMusicIntoAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertMusicIntoArtistUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.domain.model.SelectableMusicItem
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jetbrains.skia.Image
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * Class handling music fetching for desktop application.
 */
class MusicFetcherDesktopImpl(
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    isMusicAlreadySavedUseCase: IsMusicAlreadySavedUseCase,
    getArtistFromNameUseCase: GetArtistFromNameUseCase,
    getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    upsertImageCoverUseCase: UpsertImageCoverUseCase,
    upsertAlbumUseCase: UpsertAlbumUseCase,
    upsertArtistUseCase: UpsertArtistUseCase,
    upsertAlbumArtistUseCase: UpsertAlbumArtistUseCase,
    getCoverOfElementUseCase: GetCoverOfElementUseCase,
    updateAlbumCoverUseCase: UpdateAlbumCoverUseCase,
    updateArtistCoverUseCase: UpdateArtistCoverUseCase,
    upsertMusicUseCase: UpsertMusicUseCase,
    upsertFolderUseCase: UpsertFolderUseCase,
    upsertMusicIntoAlbumUseCase: UpsertMusicIntoAlbumUseCase,
    upsertMusicIntoArtistUseCase: UpsertMusicIntoArtistUseCase,
): MusicFetcher(
    isMusicAlreadySavedUseCase = isMusicAlreadySavedUseCase,
    getArtistFromNameUseCase = getArtistFromNameUseCase,
    getCorrespondingAlbumUseCase = getCorrespondingAlbumUseCase,
    upsertImageCoverUseCase = upsertImageCoverUseCase,
    upsertAlbumUseCase = upsertAlbumUseCase,
    upsertArtistUseCase = upsertArtistUseCase,
    getCoverOfElementUseCase = getCoverOfElementUseCase,
    upsertMusicIntoAlbumUseCase = upsertMusicIntoAlbumUseCase,
    upsertFolderUseCase = upsertFolderUseCase,
    upsertMusicIntoArtistUseCase = upsertMusicIntoArtistUseCase,
    upsertMusicUseCase = upsertMusicUseCase,
    updateArtistCoverUseCase = updateArtistCoverUseCase,
    upsertAlbumArtistUseCase = upsertAlbumArtistUseCase,
    updateAlbumCoverUseCase = updateAlbumCoverUseCase,
) {

    /**
     * Tries to retrieve the cover of a music from its metadata.
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
    private suspend fun extractMusicsFromCurrentDirectory(
        directory: File,
        updateProgress: (Float, String?) -> Unit,
        onMusicFetched: suspend (Music, ImageBitmap?) -> Unit,
    ) {

        // If the folder is hidden, we skip it:
        if (directory.isHidden) return

        val files = directory.listFiles() ?: return

        var count = 0f
        for (file in files) {
            count += 1f
            updateProgress(count / files.size, file.parent)
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
                    onMusicFetched(musicToAdd, musicCover)
                } catch (_: Exception) {
                    println("Failed to access information about the following file: ${file.path}")
                }
            }

            if (file.isDirectory) {
                extractMusicsFromCurrentDirectory(
                    directory = file,
                    updateProgress = updateProgress,
                    onMusicFetched = onMusicFetched,
                )
            }

        }
    }

    override suspend fun fetchMusics(updateProgress: (Float, String?) -> Unit, finishAction: () -> Unit) {
        val root = File(System.getProperty("user.home"))

        extractMusicsFromCurrentDirectory(
            directory = root,
            updateProgress = updateProgress,
            onMusicFetched = ::addMusic
        )
        upsertPlaylistUseCase(
            Playlist(
                playlistId = UUID.randomUUID(),
                name = strings.favorite,
                isFavorite = true
            )
        )
        finishAction()
    }

    override suspend fun fetchMusicsFromSelectedFolders(
        updateProgress: (Float, String?) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): ArrayList<SelectableMusicItem> {
        val newMusics = ArrayList<SelectableMusicItem>()
        val root = File(System.getProperty("user.home"))

        extractMusicsFromCurrentDirectory(
            directory = root,
            updateProgress = updateProgress,
            onMusicFetched = { music, cover ->
                if (!alreadyPresentMusicsPaths.any { it == music.path } && !hiddenFoldersPaths.any { it == music.folder }) {
                    newMusics.add(
                        SelectableMusicItem(
                            music = music,
                            cover = cover,
                            isSelected = true,
                        )
                    )
                }
            }
        )

        return newMusics
    }
}