package com.github.soulsearching.playedlist.domain

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
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
import com.github.soulsearching.domain.events.MusicEvent
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.mainpage.domain.state.MainPageState
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
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
 * Handler for managing the PlayerMusicListViewModel.
 */
class PlayerMusicListViewModelHandler(
    coroutineScope: CoroutineScope,
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
    private val settings: SoulSearchingSettings,
    playbackManager: PlaybackManager
) : ViewModelHandler {
    private var job: Job? = null

    @OptIn(ExperimentalMaterialApi::class)
    val musicListDraggableState: SwipeableState<BottomSheetStates> = SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    private var _playerMusicList: StateFlow<List<PlayerWithMusicItem>> =
        playerMusicRepository.getAllPlayerMusicsAsFlow()
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), ArrayList())
    private val _state = MutableStateFlow(MainPageState())

    var state: StateFlow<MainPageState> = combine(
        _state,
        _playerMusicList
    ) { state, playerMusicList ->
        state.copy(
            musics = playerMusicList
                .filter { it.music != null }
                .map { it.music!! } as ArrayList<Music>
        )
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        MainPageState()
    )

//    private val musicEventHandler = MusicEventHandler(
//        privateState = _state,
//        publicState = state,
//        musicRepository = musicRepository,
//        playlistRepository = playlistRepository,
//        albumRepository = albumRepository,
//        artistRepository = artistRepository,
//        musicPlaylistRepository = musicPlaylistRepository,
//        musicAlbumRepository = musicAlbumRepository,
//        musicArtistRepository = musicArtistRepository,
//        albumArtistRepository = albumArtistRepository,
//        sortType = _sortType,
//        sortDirection = _sortDirection,
//        settings = settings,
//        playbackManager = playbackManager
//    )

    /**
     * Retrieve the player music list from the database.
     */
    suspend fun getPlayerMusicList(): ArrayList<Music> {
        val playerWithMusics = playerMusicRepository.getAllPlayerMusics()

        return playerWithMusics.filter { it.music != null }.map { it.music!! } as ArrayList<Music>
    }

    /**
     * Save the player music list to the database.
     */
    fun savePlayerMusicList(musicList: List<UUID>) {
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
    fun resetPlayerMusicList() {
        CoroutineScope(Dispatchers.IO).launch {
            playerMusicRepository.deleteAllPlayerMusic()
        }
        settings.saveCurrentMusicInformation(
            currentMusicIndex = -1,
            currentMusicPosition = 0
        )
    }

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
//        musicEventHandler.handleEvent(event)
    }
}