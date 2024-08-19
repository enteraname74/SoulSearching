package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.usecase.month.GetMonthMusicListUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.elementpage.domain.toPlaylistDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedMonthViewModel(
    private val getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val updateMusicNbPlayedUseCase: UpdateMusicNbPlayedUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val getMonthMusicListUseCase: GetMonthMusicListUseCase,
) : ScreenModel, PlaylistDetailListener, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {

    private val _month: MutableStateFlow<String?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    var state = _month.flatMapLatest { month ->
        if (month == null) {
            flowOf(SelectedMonthState.Loading)
        } else {
            combine(
                getAllPlaylistWithMusicsUseCase(),
                getMonthMusicListUseCase(month = month),
            ) { allPlaylists, monthMusicList ->
                when {
                    monthMusicList == null -> SelectedMonthState.Loading
                    else -> SelectedMonthState.Data(
                        playlistDetail = monthMusicList.toPlaylistDetail(),
                        allPlaylists = allPlaylists,
                    )
                }
            }
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SelectedMonthState.Loading,
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
            getAllPlaylistsWithMusics = ::getAllPlaylistWithMusics,
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
        )
    }

    fun consumeNavigation() {
        _navigationState.value = SelectedMonthNavigationState.Idle
    }

    private fun getAllPlaylistWithMusics() =
        (state.value as? SelectedMonthState.Data)?.allPlaylists ?: emptyList()

    /**
     * Set the selected playlist.
     */
    fun init(month: String) {
        _month.value = month
    }

    override fun onEdit() {
        /* no-op */
    }

    override fun onUpdateNbPlayed(musicId: UUID) {
        screenModelScope.launch {
            updateMusicNbPlayedUseCase(musicId = musicId)
        }
    }

    override fun onUpdateNbPlayed() {
        /* no-op */
    }
}