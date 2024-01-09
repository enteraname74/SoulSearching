package com.github.soulsearching.viewmodel.handler

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.handlers.MusicEventHandler
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.util.UUID

/**
 * Handler for managing the AllMusicsViewModel.
 */
abstract class AllMusicsViewModelHandler(
    coroutineScope: CoroutineScope,
    private val musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val folderRepository: FolderRepository,
    settings: SoulSearchingSettings,
    private val musicFetcher: MusicFetcher,
    playbackManager: PlaybackManager
): ViewModelHandler {
    private val _sortType = MutableStateFlow(SortType.ADDED_DATE)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _musics = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> musicRepository.getAllMusicsSortByNameAscAsFlow()
                        SortType.ADDED_DATE -> musicRepository.getAllMusicsSortByAddedDateAscAsFlow()
                        SortType.NB_PLAYED -> musicRepository.getAllMusicsSortByNbPlayedAscAsFlow()
                        else -> musicRepository.getAllMusicsSortByNameAscAsFlow()
                    }
                }
                SortDirection.DESC -> {
                    when (sortType) {
                        SortType.NAME -> musicRepository.getAllMusicsSortByNameDescAsFlow()
                        SortType.ADDED_DATE -> musicRepository.getAllMusicsSortByAddedDateDescAsFlow()
                        SortType.NB_PLAYED -> musicRepository.getAllMusicsSortByNbPlayedDescAsFlow()
                        else -> musicRepository.getAllMusicsSortByNameDescAsFlow()
                    }
                }
                else -> musicRepository.getAllMusicsSortByNameAscAsFlow()
            }
        }
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(),
        ArrayList()
    )

    private val _state = MutableStateFlow(MusicState())

    val state = combine(
        _state,
        _musics,
        _sortType,
        _sortDirection
    ) { state, musics, sortType, sortDirection ->
        state.copy(
            musics = musics as ArrayList<Music>,
            sortType = sortType,
            sortDirection = sortDirection
        )
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        MusicState()
    )

    private val musicEventHandler = MusicEventHandler(
        privateState = _state,
        publicState = state,
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        albumArtistRepository = albumArtistRepository,
        imageCoverRepository = imageCoverRepository,
        sortDirection = _sortDirection,
        sortType = _sortType,
        settings = settings,
        playbackManager = playbackManager
    )

    /**
     * Persist a music and its cover.
     */
    suspend fun addMusic(musicToAdd: Music, musicCover: ImageBitmap?) {
        // Si la musique a déjà été enregistrée, on ne fait rien :
        val existingMusic = musicRepository.getMusicFromPath(musicToAdd.path)
        if (existingMusic != null) {
            return
        }

        val correspondingArtist = artistRepository.getArtistFromInfo(
            artistName = musicToAdd.artist
        )
        // Si l'artiste existe, on regarde si on trouve un album correspondant :
        val correspondingAlbum = if (correspondingArtist == null) {
            null
        } else {
            albumRepository.getCorrespondingAlbum(
                albumName = musicToAdd.album,
                artistId = correspondingArtist.artistId
            )
        }
        val albumId = correspondingAlbum?.albumId ?: UUID.randomUUID()
        val artistId = correspondingArtist?.artistId ?: UUID.randomUUID()
        if (correspondingAlbum == null) {
            val coverId = UUID.randomUUID()
            if (musicCover != null) {
                musicToAdd.coverId = coverId
                imageCoverRepository.insertImageCover(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
            }

            albumRepository.insertAlbum(
                Album(
                    coverId = if (musicCover != null) coverId else null,
                    albumId = albumId,
                    albumName = musicToAdd.album
                )
            )
            artistRepository.insertArtist(
                Artist(
                    coverId = if (musicCover != null) coverId else null,
                    artistId = artistId,
                    artistName = musicToAdd.artist
                )
            )
            albumArtistRepository.insertAlbumIntoArtist(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId
                )
            )
        } else {
            // On ajoute si possible la couverture de l'album de la musique :
            val albumCover = if (correspondingAlbum.coverId != null) {
                imageCoverRepository.getCoverOfElement(coverId = correspondingAlbum.coverId!!)
            } else {
                null
            }
            val shouldPutAlbumCoverWithMusic = (albumCover != null)
            val shouldUpdateArtistCover =
                (correspondingArtist?.coverId == null) && ((albumCover != null) || (musicCover != null))

            if (shouldPutAlbumCoverWithMusic) {
                musicToAdd.coverId = albumCover?.coverId
            } else if (musicCover != null) {
                val coverId = UUID.randomUUID()
                musicToAdd.coverId = coverId
                imageCoverRepository.insertImageCover(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
                // Dans ce cas, l'album n'a pas d'image, on lui en ajoute une :
                albumRepository.updateAlbumCover(
                    newCoverId = coverId,
                    albumId = correspondingAlbum.albumId
                )
            }

            if (shouldUpdateArtistCover) {
                val newArtistCover: UUID? = if (shouldPutAlbumCoverWithMusic) {
                    albumCover?.coverId
                } else {
                    musicToAdd.coverId
                }
                if (correspondingArtist != null && newArtistCover != null) {
                    artistRepository.updateArtistCover(
                        newCoverId = newArtistCover,
                        artistId = correspondingArtist.artistId
                    )
                }
            }
        }
        musicRepository.insertMusic(musicToAdd)
        folderRepository.insertFolder(
            Folder(
                folderPath = musicToAdd.folder
            )
        )
        musicAlbumRepository.insertMusicIntoAlbum(
            MusicAlbum(
                musicId = musicToAdd.musicId,
                albumId = albumId
            )
        )
        musicArtistRepository.insertMusicIntoArtist(
            MusicArtist(
                musicId = musicToAdd.musicId,
                artistId = artistId
            )
        )
    }

    /**
     * Check if a music is in the favorites.
     */
    suspend fun isMusicInFavorite(musicId: UUID): Boolean {
        return musicRepository.getMusicFromFavoritePlaylist(musicId = musicId) != null
    }

    /**
     * Retrieve the artist id of a music.
     */
    fun getArtistIdFromMusicId(musicId: UUID): UUID? {
        return runBlocking(context = Dispatchers.IO) { musicArtistRepository.getArtistIdFromMusicId(musicId) }
    }

    /**
     * Retrieve the album id of a music.
     */
    fun getAlbumIdFromMusicId(musicId: UUID): UUID? {
        return runBlocking(context = Dispatchers.IO) { musicAlbumRepository.getAlbumIdFromMusicId(musicId) }
    }

    /**
     * Fetch all musics.
     */
    suspend fun fetchMusics(
        updateProgress: (Float) -> Unit,
        finishAction: () -> Unit
    ) {
        musicFetcher.fetchMusics(
            updateProgress = updateProgress,
            finishAction = finishAction
        )
    }

    /**
     * Check all musics and delete the one that does not exists (if the path of the music is not valid).
     */
    abstract fun checkAndDeleteMusicIfNotExist()

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }
}