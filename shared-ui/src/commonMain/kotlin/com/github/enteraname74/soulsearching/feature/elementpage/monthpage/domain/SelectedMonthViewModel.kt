package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.MonthMusicList
import com.github.enteraname74.soulsearching.domain.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*

class SelectedMonthViewModel(
    private val getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getAllMusicUseCase: GetAllMusicUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
) : ScreenModel, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {
    @OptIn(ExperimentalCoroutinesApi::class)
    var state = getAllPlaylistWithMusicsUseCase().mapLatest { playlists ->
        SelectedMonthState(
            allPlaylists = playlists,
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedMonthState()
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistBottomSheet: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistBottomSheet: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistBottomSheet.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedMonthNavigationState> = MutableStateFlow(
        SelectedMonthNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedMonthNavigationState> = _navigationState.asStateFlow()

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = { _navigationState.value = SelectedMonthNavigationState.ToModifyMusic(it) },
            getAllPlaylistsWithMusics = { state.value.allPlaylists },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
        )
    }

    fun consumeNavigation() {
        _navigationState.value = SelectedMonthNavigationState.Idle
    }

    /**
     * Handles events of the selected folder screen.
     */
    fun onEvent(event: SelectedMonthEvent) {
        when (event) {
            is SelectedMonthEvent.SetSelectedMonth -> setSelectedFolder(month = event.month)
            is SelectedMonthEvent.AddNbPlayed -> incrementNbPlayed(playlistId = event.playlistId)
        }
    }

    /**
     * Set the selected playlist.
     */
    private fun setSelectedFolder(month: String) {
        state = combine(
            getAllMusicUseCase()
                .map { allMusics ->
                    val musics = allMusics.filter { Utils.getMonthAndYearOfDate(date = it.addedDate) == month  }
                    MonthMusicList(
                        month = month,
                        musics = musics,
                        coverId = musics.firstOrNull { it.coverId != null }?.coverId
                    )
                },
            getAllPlaylistWithMusicsUseCase()
        ) { monthMusics, playlists ->
            state.value.copy(
                monthMusicList = monthMusics,
                allPlaylists = playlists
            )
        }.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedMonthState()
        )
    }

    /**
     * Increment by one the number of time a playlist was played.
     */
    private fun incrementNbPlayed(playlistId: UUID) {}
}