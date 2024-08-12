package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
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
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsNavigationState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

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
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
) : ScreenModel, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {
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
        AllMusicsState(
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
        AllMusicsState()
    )

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistsBottomSheetState: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistsBottomSheetState: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistsBottomSheetState.asStateFlow()

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _navigationState: MutableStateFlow<AllMusicsNavigationState> = MutableStateFlow(
        AllMusicsNavigationState.Idle
    )
    val navigationState: StateFlow<AllMusicsNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        _navigationState.value = AllMusicsNavigationState.Idle
    }

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistsBottomSheetState.value = it },
            getAllPlaylistsWithMusics = { state.value.allPlaylists },
            onModifyMusic = { _navigationState.value = AllMusicsNavigationState.ToModifyMusic(it) }
        )
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
            is MusicEvent.SetSortType -> setSortType(newSortType = event.type)
            is MusicEvent.SetSortDirection -> setSortDirection(newSortDirection = event.direction)
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
}