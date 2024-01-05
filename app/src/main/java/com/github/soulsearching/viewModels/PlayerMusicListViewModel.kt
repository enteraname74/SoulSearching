package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.utils.EventUtils
import com.github.soulsearching.classes.utils.SharedPrefUtils
import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.PlayerMusic
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * View model for the player music list view.
 */
@HiltViewModel
class PlayerMusicListViewModel @Inject constructor(
    private val playerMusicDao: PlayerMusicDao,
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
    private var job: Job? = null
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)

    private var _playerMusicList = playerMusicDao.getAllPlayerMusicsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private val _state = MutableStateFlow(MusicState())

    val state = combine(
        _state,
        _playerMusicList
    ) { state, playerMusicList ->
        state.copy(
            musics = playerMusicList
                .filter { it.music != null }
                .map { it.music!! } as ArrayList<Music>
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MusicState()
    )

    /**
     * Retrieve the player music list from the database.
     */
    suspend fun getPlayerMusicList(): ArrayList<Music> {
        val playerWithMusics = playerMusicDao.getAllPlayerMusics()

        return playerWithMusics.filter { it.music != null }.map { it.music!! } as ArrayList<Music>
    }

    /**
     * Save the player music list to the database.
     */
    fun savePlayerMusicList(musicList : ArrayList<UUID>) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            playerMusicDao.deleteAllPlayerMusic()
            for (id in musicList) {
                playerMusicDao.insertPlayerMusic(
                    PlayerMusic = PlayerMusic(
                        playerMusicId = id
                    )
                )
            }
        }
    }

    /**
     * Reset the player music list to the database.
     */
    fun resetPlayerMusicList() {
        CoroutineScope(Dispatchers.IO).launch {
            playerMusicDao.deleteAllPlayerMusic()
        }
        SharedPrefUtils.setPlayerSavedCurrentMusic()
    }

    /**
     * Manage music events.
     */
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