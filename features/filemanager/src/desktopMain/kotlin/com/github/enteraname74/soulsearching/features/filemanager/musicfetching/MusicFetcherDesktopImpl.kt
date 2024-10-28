package com.github.enteraname74.soulsearching.features.filemanager.musicfetching

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.playlist.GetFavoritePlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.coreui.strings.strings
import kotlinx.coroutines.flow.first
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import java.net.URLConnection
import java.nio.file.Files
import java.util.*


/**
 * Class handling music fetching for desktop application.
 */
internal class MusicFetcherDesktopImpl(
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val getFavoritePlaylistWithMusicsUseCase: GetFavoritePlaylistWithMusicsUseCase,
) : MusicFetcher() {

    private val foldersNamesBlackList: List<String> = listOf(
        "node_modules"
    )

    private fun isMusicFile(file: File): Boolean {
        val mimeType: String = URLConnection.guessContentTypeFromName(file.name) ?: return false
        val authorizedMimeTypes =
            listOf(
                "audio/mpeg",        // MP3 files
                "audio/mp4",         // MP4 files with audio (M4A)
                "audio/wav",         // WAV files
                "audio/ogg",         // Ogg Vorbis files
                "audio/flac",        // FLAC files
                "audio/aac",         // AAC files
                "audio/opus",        // Opus files
                "audio/x-ms-wma",    // Windows Media Audio files
                "audio/aiff",        // AIFF files
                "audio/webm",        // WebM files with audio
                "audio/amr",         // AMR files
                "audio/vnd.rn-realaudio", // RealAudio files
                "audio/midi",        // MIDI files
                "audio/3gpp",        // 3GP files with audio
                "audio/x-m4a",       // M4A files
                "audio/x-xmf",       // XMF files
                "audio/x-ms-asf",    // ASF files with audio
                "audio/x-wav",       // RAW audio format (often used for WAV)
                "audio/eac3"         // Enhanced AC-3 files
            )
        return authorizedMimeTypes.contains(mimeType)
    }

    /**
     * Extract mp3 files from current directory.
     */
    private suspend fun extractMusicsFromCurrentDirectory(
        directory: File,
        updateProgress: (Float, String?) -> Unit,
        onMusicFetched: suspend (Music) -> Unit,
    ) {

        // If the folder is hidden or is in the black list, we skip it:
        if (directory.isHidden || directory.name in foldersNamesBlackList) return

        val files = directory.listFiles() ?: return

        var count = 0f
        for (file in files) {
            count += 1f
            updateProgress(count / files.size, file.parent)
            if (file.isHidden || !file.canRead() || Files.isSymbolicLink(file.toPath())) {
                continue
            } else if (isMusicFile(file = file)) {
                try {
                    val audioFile: AudioFile = AudioFileIO.read(file)
                    val tag: Tag = audioFile.tag

                    val musicToAdd = Music(
                        name = tag.getFirst(FieldKey.TITLE),
                        album = tag.getFirst(FieldKey.ALBUM),
                        artist = tag.getFirst(FieldKey.ARTIST),
                        duration = (audioFile.audioHeader.trackLength * 1_000).toLong(),
                        path = file.path,
                        folder = file.parent,
                        cover = Cover.CoverFile(
                            initialCoverPath = file.path,
                        )
                    )
                    onMusicFetched(musicToAdd)
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

    override suspend fun fetchMusics(
        updateProgress: (Float, String?) -> Unit,
    ): Boolean {
        val root = File(System.getProperty("user.home"))
        init()
        extractMusicsFromCurrentDirectory(
            directory = root,
            updateProgress = updateProgress,
            onMusicFetched = ::addMusic
        )
        if (getFavoritePlaylistWithMusicsUseCase().first() == null) {
            upsertPlaylistUseCase(
                Playlist(
                    playlistId = UUID.randomUUID(),
                    name = strings.favorite,
                    isFavorite = true
                )
            )
        }
        return saveAllWithMultipleArtistsCheck()
    }

    override suspend fun fetchMusicsFromSelectedFolders(
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): List<SelectableMusicItem> {
        val newMusics = ArrayList<SelectableMusicItem>()
        val root = File(System.getProperty("user.home"))

        extractMusicsFromCurrentDirectory(
            directory = root,
            updateProgress = { _, _ -> },
            onMusicFetched = { music ->
                if (!alreadyPresentMusicsPaths.any { it == music.path } && !hiddenFoldersPaths.any { it == music.folder }) {
                    newMusics.add(
                        SelectableMusicItem(
                            music = music,
                            isSelected = true,
                        )
                    )
                }
            }
        )

        return newMusics
    }
}