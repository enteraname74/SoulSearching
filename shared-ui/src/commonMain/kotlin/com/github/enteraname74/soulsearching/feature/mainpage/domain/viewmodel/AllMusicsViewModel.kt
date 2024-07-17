package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.MusicBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.events.MusicEvent
import com.github.enteraname74.soulsearching.domain.model.MonthMusicList
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.domain.model.MusicFolder
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.utils.Utils
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainScreenNavigationState
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.dialog.DeleteMusicDialog
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * Handler for managing the AllMusicsViewModel.
 */
@Suppress("Deprecation")
class AllMusicsViewModel(
    private val settings: SoulSearchingSettings,
    private val playbackManager: PlaybackManager,
    private val getAllMusicsSortedUseCase: GetAllMusicsSortedUseCase,
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val musicFetcher: MusicFetcher,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    private val upsertMusicUseCase: UpsertMusicUseCase,
    private val upsertMusicIntoPlaylistUseCase: UpsertMusicIntoPlaylistUseCase,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : ScreenModel {
    var currentPage by mutableStateOf<ElementEnum?>(null)
    private val _sortType = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_MUSICS_TYPE_KEY, SortType.NAME
        )
    )
    private val _sortDirection = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_MUSICS_DIRECTION_KEY, SortDirection.ASC
        )
    )

    @OptIn(ExperimentalMaterialApi::class)
    val searchDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _musics = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            getAllMusicsSortedUseCase(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }
    }.stateIn(
        screenModelScope,
        SharingStarted.Eagerly,
        ArrayList()
    )

    val state = combine(
        _musics,
        _sortType,
        _sortDirection,
        getAllPlaylistWithMusicsUseCase()
    ) { musics, sortType, sortDirection, playlists ->
        MainPageState(
            musics = musics,
            sortType = sortType,
            sortDirection = sortDirection,
            allPlaylists = playlists,
            monthMusics = buildMonthMusics(allMusics = musics),
            folderMusics = buildMusicFolders(allMusics = musics)
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        MainPageState()
    )

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistsBottomSheetState: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistsBottomSheetState: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistsBottomSheetState.asStateFlow()

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _navigationState: MutableStateFlow<MainScreenNavigationState> = MutableStateFlow(
        MainScreenNavigationState.Idle
    )
    val navigationState: StateFlow<MainScreenNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        _navigationState.value = MainScreenNavigationState.Idle
    }

    /**
     * Build a list of music folders.
     */
    private fun buildMusicFolders(allMusics: List<Music>): List<MusicFolder> {
        return allMusics.groupBy { it.folder }.entries.map { (folder, musics) ->
            MusicFolder(
                path = folder,
                musics = musics,
                coverId = musics.firstOrNull { it.coverId != null }?.coverId
            )
        }
    }

    /**
     * Build a list of month musics.
     */
    private fun buildMonthMusics(allMusics: List<Music>): List<MonthMusicList> {
        return allMusics.groupBy { Utils.getMonthAndYearOfDate(date = it.addedDate) }.entries.map { (date, musics) ->
            MonthMusicList(
                month = date,
                musics = musics,
                coverId = musics.firstOrNull { it.coverId != null }?.coverId
            )
        }
    }

    private fun showDeleteMusicDialog(musicToDelete: Music) {
        _dialogState.value = DeleteMusicDialog(
            musicToDelete = musicToDelete,
            onDelete = {
                screenModelScope.launch {
                    deleteMusicUseCase(musicToDelete)
                }
            },
            onClose = { _dialogState.value = null }
        )
    }

    /**
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(music: Music, selectedPlaylistsIds: List<UUID>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylistId in selectedPlaylistsIds) {
                upsertMusicIntoPlaylistUseCase(
                    MusicPlaylist(
                        musicId = music.musicId,
                        playlistId = selectedPlaylistId
                    )
                )
            }
            playbackManager.updateMusic(music = music)
        }
    }

    private fun showAddToPlaylistsBottomSheet(musicToAdd: Music) {
        _addToPlaylistsBottomSheetState.value = AddToPlaylistBottomSheet(
            onClose = { _addToPlaylistsBottomSheetState.value = null },
            addMusicToSelectedPlaylists = { selectedPlaylistsIds ->
                addMusicToPlaylists(
                    music = musicToAdd,
                    selectedPlaylistsIds = selectedPlaylistsIds,
                )
            },
            playlistsWithMusics = state.value.allPlaylists.filter {
                println("FILTER: ${it.musics.none { music -> music.musicId == musicToAdd.musicId }}")
                it.musics.none { music -> music.musicId == musicToAdd.musicId }
            }
        )
    }

    fun showMusicBottomSheet(selectedMusic: Music) {
        _bottomSheetState.value = MusicBottomSheet(
            selectedMusic = selectedMusic,
            onClose = { _bottomSheetState.value = null },
            onDeleteMusic = { showDeleteMusicDialog(musicToDelete = selectedMusic) },
            onModifyMusic = { _navigationState.value = MainScreenNavigationState.ToModifyMusic(selectedMusic) },
            onRemoveFromPlaylist = { /*no-op*/ },
            onAddToPlaylist = { showAddToPlaylistsBottomSheet(musicToAdd = selectedMusic) },
            toggleQuickAccess = {
                screenModelScope.launch {
                    upsertMusicUseCase(
                        music = selectedMusic.copy(
                            isInQuickAccess = !selectedMusic.isInQuickAccess,
                        )
                    )
                }
            }
        )
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
     * Check all musics and delete the one that does not exist (if the path of the music is not valid).
     */
    fun checkAndDeleteMusicIfNotExist() {
        CoroutineScope(Dispatchers.IO).launch {
            var deleteCount = 0
            for (music in state.value.musics) {
                if (!File(music.path).exists()) {
                    playbackManager.removeSongFromPlayedPlaylist(
                        music.musicId
                    )
                    deleteMusicUseCase(music)
                    deleteCount += 1
                }
            }

            if (deleteCount > 0) {
                feedbackPopUpManager.showFeedback(
                    feedback = strings.deletedMusicsAutomatically(total = deleteCount)
                )
            }
        }
    }

    /**
     * handle a music event.
     */
    fun onMusicEvent(event: MusicEvent) {
        when(event) {
            is MusicEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is MusicEvent.SetSortType -> setSortType(newSortType = event.type)
            is MusicEvent.SetSortDirection -> setSortDirection(newSortDirection = event.direction)
            is MusicEvent.SetFavorite -> toggleFavoriteState(musicId = event.musicId)
            is MusicEvent.ToggleQuickAccessState -> toggleQuickAccessState(music = event.music)
        }
    }


    /**
     * Remove the selected music, from the MusicState, from the application
     */
    private fun deleteMusicFromApp(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteMusicUseCase(musicId = musicId)
            playbackManager.removeSongFromLists(musicId = musicId)
        }
    }

    /**
     * Set the sort type.
     */
    private fun setSortType(newSortType: Int) {
        _sortType.value = newSortType
        settings.setInt(
            key = SoulSearchingSettings.SORT_MUSICS_TYPE_KEY,
            value = newSortType
        )
    }

    /**
     * Set the sort direction.
     */
    private fun setSortDirection(newSortDirection: Int) {
        _sortDirection.value = newSortDirection
        settings.setInt(
            key = SoulSearchingSettings.SORT_MUSICS_DIRECTION_KEY,
            value = newSortDirection
        )
    }

    /**
     * Toggle the favorite status of a given music.
     */
    private fun toggleFavoriteState(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            toggleMusicFavoriteStatusUseCase(musicId = musicId)
        }
    }

    /**
     * Toggle the quick access state of the selected music.
     */
    private fun toggleQuickAccessState(music: Music) {
        CoroutineScope(Dispatchers.IO).launch {
            upsertMusicUseCase(
                music = music.copy(
                    isInQuickAccess = !music.isInQuickAccess,
                )
            )
        }
    }
}