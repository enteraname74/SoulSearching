package com.github.soulsearching.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.EventUtils
import com.github.soulsearching.classes.SortDirection
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.*
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AllMusicsViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val playlistDao: PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val imageCoverDao: ImageCoverDao,
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.ADDED_DATE)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _musics = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> musicDao.getAllMusicsSortByNameAsc()
                        SortType.ADDED_DATE -> musicDao.getAllMusicsSortByAddedDateAsc()
                        SortType.NB_PLAYED -> musicDao.getAllMusicsSortByNbPlayedAsc()
                        else -> musicDao.getAllMusicsSortByNameAsc()
                    }
                }
                SortDirection.DESC -> {
                    when (sortType) {
                        SortType.NAME -> musicDao.getAllMusicsSortByNameDesc()
                        SortType.ADDED_DATE -> musicDao.getAllMusicsSortByAddedDateDesc()
                        SortType.NB_PLAYED -> musicDao.getAllMusicsSortByNbPlayedDesc()
                        else -> musicDao.getAllMusicsSortByNameDesc()
                    }
                }
                else -> musicDao.getAllMusicsSortByNameAsc()
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


    suspend fun addMusic(musicToAdd: Music, musicCover: Bitmap?) {
        // Si la musique a déjà été enregistrée, on ne fait rien :
        val existingMusic = musicDao.getMusicFromPath(musicToAdd.path)
        if (existingMusic != null) {
            return
        }

        val correspondingArtist = artistDao.getArtistFromInfo(
            artistName = musicToAdd.artist
        )
        // Si l'artiste existe, on regarde si on trouve un album correspondant :
        val correspondingAlbum = if (correspondingArtist == null) {
            null
        } else {
            albumDao.getCorrespondingAlbum(
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
                imageCoverDao.insertImageCover(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
            }

            albumDao.insertAlbum(
                Album(
                    coverId = if (musicCover != null) coverId else null,
                    albumId = albumId,
                    albumName = musicToAdd.album
                )
            )
            artistDao.insertArtist(
                Artist(
                    coverId = if (musicCover != null) coverId else null,
                    artistId = artistId,
                    artistName = musicToAdd.artist
                )
            )
            albumArtistDao.insertAlbumIntoArtist(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId
                )
            )
        } else {
            // On ajoute si possible la couverture de l'album de la musique :
            val imageCover = if (correspondingAlbum.coverId != null) {
                imageCoverDao.getCoverOfElement(coverId = correspondingAlbum.coverId!!)
            } else {
                null
            }
            if (imageCover != null) {
                musicToAdd.coverId = imageCover.coverId
            } else if (musicCover != null) {
                val coverId = UUID.randomUUID()
                musicToAdd.coverId = coverId
                imageCoverDao.insertImageCover(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
            }
        }
        musicDao.insertMusic(musicToAdd)
        musicAlbumDao.insertMusicIntoAlbum(
            MusicAlbum(
                musicId = musicToAdd.musicId,
                albumId = albumId
            )
        )
        musicArtistDao.insertMusicIntoArtist(
            MusicArtist(
                musicId = musicToAdd.musicId,
                artistId = artistId
            )
        )
    }

    suspend fun isMusicInFavorite(musicId: UUID): Boolean {
        return musicDao.getMusicFromFavoritePlaylist(musicId = musicId) != null
    }

    fun onMusicEvent(event: MusicEvent) {
        EventUtils.onMusicEvent(
            event = event,
            _state = _state,
            state = state,
            musicDao = musicDao,
            playlistDao = playlistDao,
            albumDao = albumDao,
            artistDao = artistDao,
            musicPlaylistDao = musicPlaylistDao,
            musicAlbumDao = musicAlbumDao,
            musicArtistDao = musicArtistDao,
            albumArtistDao = albumArtistDao,
            _sortDirection = _sortDirection,
            _sortType = _sortType,
            imageCoverDao = imageCoverDao
        )
    }
}