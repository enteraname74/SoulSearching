package com.github.soulsearching.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.model.Album
import com.github.enteraname74.model.AlbumArtist
import com.github.enteraname74.model.Artist
import com.github.enteraname74.model.Folder
import com.github.enteraname74.model.ImageCover
import com.github.enteraname74.model.Music
import com.github.enteraname74.model.MusicAlbum
import com.github.enteraname74.model.MusicArtist
import com.github.enteraname74.repository.AlbumArtistRepository
import com.github.enteraname74.repository.AlbumRepository
import com.github.enteraname74.repository.ArtistRepository
import com.github.enteraname74.repository.FolderRepository
import com.github.enteraname74.repository.ImageCoverRepository
import com.github.enteraname74.repository.MusicAlbumRepository
import com.github.enteraname74.repository.MusicArtistRepository
import com.github.enteraname74.repository.MusicPlaylistRepository
import com.github.enteraname74.repository.MusicRepository
import com.github.enteraname74.repository.PlaylistRepository
import com.github.soulsearching.R
import com.github.soulsearching.classes.MusicEventHandler
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.classes.utils.MusicFetcher
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * View model for managing all musics.
 */
class AllMusicsViewModel(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {
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
        viewModelScope,
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
        viewModelScope,
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
        sortType = _sortType
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
        context: Context,
        updateProgress: (Float) -> Unit,
        finishAction: () -> Unit
    ) {
        val musicFetcher = MusicFetcher(
            context = context,
            musicRepository = musicRepository,
            playlistRepository = playlistRepository,
            albumRepository = albumRepository,
            artistRepository = artistRepository,
            musicAlbumRepository = musicAlbumRepository,
            musicArtistRepository = musicArtistRepository,
            albumArtistRepository = albumArtistRepository,
            imageCoverRepository = imageCoverRepository,
            folderRepository = folderRepository
        )
        musicFetcher.fetchMusics(
            updateProgress = updateProgress,
            finishAction = finishAction
        )
    }

    /**
     * Check all musics and delete the one that does not exists (if the path of the music is not valid).
     */
    fun checkAndDeleteMusicIfNotExist(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            var deleteCount = 0
            for (music in state.value.musics) {
                if (!File(music.path).exists()) {
                    PlayerUtils.playerViewModel.removeMusicFromCurrentPlaylist(
                        music.musicId,
                        context
                    )
                    musicRepository.deleteMusic(music)
                    deleteCount += 1
                }
            }

            if (deleteCount == 1) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.deleted_music),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (deleteCount > 1) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.deleted_musics, deleteCount),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }
}