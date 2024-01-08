package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerMusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.classes.MusicEventHandler
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.classes.utils.SharedPrefUtils
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * View model for the player music list view.
 */
class PlayerMusicListViewModelImpl(
    private val playerMusicRepository: PlayerMusicRepository,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
) : ViewModel(), PlayerMusicListViewModel {
    private var job: Job? = null
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)

    private var _playerMusicList: StateFlow<List<PlayerWithMusicItem>> =
        playerMusicRepository.getAllPlayerMusicsAsFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private val _state = MutableStateFlow(MusicState())

    override var state: StateFlow<MusicState> = combine(
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
     * Retrieve the player music list from the database.
     */
    override suspend fun getPlayerMusicList(): ArrayList<Music> {
        val playerWithMusics = playerMusicRepository.getAllPlayerMusics()

        return playerWithMusics.filter { it.music != null }.map { it.music!! } as ArrayList<Music>
    }

    /**
     * Save the player music list to the database.
     */
    override fun savePlayerMusicList(musicList: ArrayList<UUID>) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            playerMusicRepository.deleteAllPlayerMusic()
            for (id in musicList) {
                playerMusicRepository.insertPlayerMusic(
                    playerMusic = PlayerMusic(
                        playerMusicId = id
                    )
                )
            }
        }
    }

    /**
     * Reset the player music list to the database.
     */
    override fun resetPlayerMusicList() {
        CoroutineScope(Dispatchers.IO).launch {
            playerMusicRepository.deleteAllPlayerMusic()
        }
        SharedPrefUtils.setPlayerSavedCurrentMusic()
    }

    /**
     * Manage music events.
     */
    override fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }
}