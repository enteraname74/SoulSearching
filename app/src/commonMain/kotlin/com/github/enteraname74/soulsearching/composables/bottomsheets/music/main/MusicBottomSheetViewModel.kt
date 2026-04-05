package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicScope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMultiMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MusicBottomSheetViewModel(
    private val playerMusicListViewManager: PlayerMusicListViewManager,
    private val playerViewManager: PlayerViewManager,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val multiSelectionManager: MultiSelectionManager,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val commonMusicPlaylistUseCase: CommonMusicPlaylistUseCase,
    private val playbackManager: PlaybackManager,
    private val navScope: MusicBottomSheetNavScope,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    settings: SoulSearchingSettings,
    params: MusicBottomSheetDestination,
) : ViewModel() {
    private val musicIds: List<UUID> = params.musicIds
    private val playlistId: UUID? = params.playlistId

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<MusicBottomSheetState> = combine(
        commonMusicUseCase.getFromIds(musicIds),
        playbackManager.playedList,
        playbackManager.state,
        dialogState,
        settings.getFlowOn(
            settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
        ),
        hasValidCloudInformationUseCase(),
        playbackManager.currentScope,
    ) { data ->
        val musics = data[0] as List<Music>
        val playedList = data[1] as List<Music>
        val playbackState = data[2] as PlaybackManagerState
        val dialogState = data[3] as SoulDialog?
        val isQuickAccessShown = data[4] as Boolean
        val hasValidCloudInformation = data[5] as Boolean
        val playedListScope = data[6] as PlayedListScope?

        MusicBottomSheetState(
            musics = musics,
            bottomSheetTopInformation = buildTopInformation(musics),
            rowSpecs = buildRowSpecs(
                musics = musics,
                playedList = playedList,
                currentPlayedMusic = (playbackState as? PlaybackManagerState.Data)?.currentMusic,
                isQuickAccessShown = isQuickAccessShown,
                hasValidCloudInformation = hasValidCloudInformation,
                playedListScope = playedListScope,
            ),
            dialogState = dialogState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MusicBottomSheetState(),
    )

    // TODO CLOUD: Add scope checks
    private fun buildRowSpecs(
        musics: List<Music>,
        playedList: List<Music>,
        currentPlayedMusic: Music?,
        isQuickAccessShown: Boolean,
        hasValidCloudInformation: Boolean,
        playedListScope: PlayedListScope?,
    ): List<BottomSheetRowSpec> = buildList {
        val editEnabled: Boolean = musics.size == 1 && musics.first().scope == MusicScope.User
        val canAddNext: Boolean = when {
            playedListScope?.isRemote == true -> false
            musics.size == 1 -> {
                val isSameMusic =
                    currentPlayedMusic != null && musics.first().musicId == currentPlayedMusic.musicId
                !isSameMusic || playedList.isEmpty()
            }

            else -> true
        }

        val canAddToQueue: Boolean = when {
            musics.size == 1 -> {
                val isSameMusic =
                    currentPlayedMusic != null && musics.first().musicId == currentPlayedMusic.musicId
                !isSameMusic || playedList.isEmpty()
            }

            else -> true
        }

        val removeFromPlayedList: Boolean = when {
            musics.size == 1 && (musics.first().scope == MusicScope.User || playedListScope?.isAdmin == true) ->
                playedList.any { it.musicId == musics.first().musicId }

            else -> playedList.isNotEmpty()
        }

        val canAddToPlaylist = when {
            musics.size == 1 && musics.first().scope != MusicScope.User -> false
            else -> true
        }

        val canAddToQuickAccess = when {
            !isQuickAccessShown -> false
            musics.size == 1 && musics.first().scope != MusicScope.User -> false
            else -> true
        }

        val canDelete = when {
            musics.size == 1 && musics.first().scope != MusicScope.User -> false
            else -> true
        }

        if (canAddToQuickAccess) {
            val isInQuickAccess: Boolean = if (musics.size == 1) {
                musics.first().isInQuickAccess
            } else {
                musics.all { it.isInQuickAccess }
            }
            add(
                BottomSheetRowSpec.quickAccess(
                    onClick = { handleQuickAccess(!isInQuickAccess) },
                    isInQuickAccess = if (musics.size == 1) {
                        musics.first().isInQuickAccess
                    } else {
                        musics.all { it.isInQuickAccess }
                    }
                )
            )
        }

        if (canAddToPlaylist) {
            add(
                BottomSheetRowSpec.addToPlaylist(
                    onClick = ::addToPlaylists
                )
            )
        }

        if (editEnabled) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_edit_filled,
                    title = strings.modifyMusic,
                    onClick = ::toModifyMusic,
                )
            )
        }
        if (canAddNext) {
            add(BottomSheetRowSpec.playNext(::playNext))
        }

        if (canAddToQueue) {
            add(BottomSheetRowSpec.addToQueue(::addToQueue))
        }

        if (hasValidCloudInformation) {
            add(BottomSheetRowSpec.startSharedPlayedList(::startSharedPlayedList))
        }

        if (removeFromPlayedList) {
            add(
                BottomSheetRowSpec.removeFromPlayedList(
                    onClick = ::removeFromPlayedList,
                )
            )
        }

        if (playlistId != null) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_delete_filled,
                    title = strings.removeFromPlaylist,
                    onClick = ::showRemoveFromPlaylistDialog,
                )
            )
        }

        if (canDelete) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_delete_filled,
                    title = if (musics.size == 1) {
                        strings.deleteMusic
                    } else {
                        strings.deleteSelectedMusics
                    },
                    onClick = ::showDeleteDialog,
                )
            )
        }
    }

    private fun buildTopInformation(musics: List<Music>): BottomSheetTopInformation =
        if (musics.size == 1) {
            val music = musics.first()
            BottomSheetTopInformation(
                title = music.name,
                subTitle = music.informationText,
                cover = music.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = musics.size),
                cover = null,
            )
        }

    private fun minimisePlayerViewsIfNeeded() {
        playerMusicListViewManager.closeIfPossible()
        playerViewManager.minimiseIfPossible()
    }

    private fun deleteMusics() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            dialogState.value = null
            when (val result = playbackManager.removeSongsFromPlayedPlaylist(musicIds)) {
                is SoulResult.Error<*> -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> {
                    deleteMusicUseCase(musicIds = musicIds)
                    multiSelectionManager.clearMultiSelection()
                    navScope.navigateBack()
                }
            }
        }
    }

    private fun removeFromPlaylist() {
        if (playlistId == null) return

        viewModelScope.launch {
            loadingManager.withLoading {
                musicIds.forEach { musicId ->
                    commonMusicPlaylistUseCase.delete(
                        musicId = musicId,
                        playlistId = playlistId,
                    )
                }
            }
            dialogState.value = null
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun toModifyMusic() {
        musicIds.firstOrNull()?.let {
            viewModelScope.launch {
                minimisePlayerViewsIfNeeded()
                multiSelectionManager.clearMultiSelection()
                navScope.toModifyMusic(it)
            }
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonMusicUseCase.upsertAll(
                    allMusics = state.value.musics.map {
                        it.copy(
                            isInQuickAccess = newValue,
                        )
                    }
                )
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun showDeleteDialog() {
        dialogState.value = if (state.value.musics.size == 1) {
            DeleteMusicDialog(
                onDelete = { deleteMusics() },
                onClose = { dialogState.value = null }
            )
        } else {
            DeleteMultiMusicDialog(
                onDelete = { deleteMusics() },
                onClose = { dialogState.value = null }
            )
        }
    }

    private fun showRemoveFromPlaylistDialog() {
        if (playlistId == null) return
        dialogState.value = if (state.value.musics.size == 1) {
            RemoveMusicFromPlaylistDialog(
                onConfirm = ::removeFromPlaylist,
                onClose = { dialogState.value = null }
            )
        } else {
            RemoveMultiMusicFromPlaylistDialog(
                onConfirm = ::removeFromPlaylist,
                onClose = { dialogState.value = null }
            )
        }
    }

    private fun addToPlaylists() {
        navScope.toAddToPlaylists(musicIds)
    }

    private fun removeFromPlayedList() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.removeSongsFromPlayedPlaylist(musicIds)
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun playNext() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.addMultipleMusicsToPlayNext(state.value.musics)
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun addToQueue() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.addMultipleMusicsToQueue(state.value.musics)
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun startSharedPlayedList() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.startSharedList(state.value.musics.map { it.musicId })
            when (result) {
                is SoulResult.Error -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> {
                    multiSelectionManager.clearMultiSelection()
                    navScope.navigateBack()
                }
            }
        }
    }
}