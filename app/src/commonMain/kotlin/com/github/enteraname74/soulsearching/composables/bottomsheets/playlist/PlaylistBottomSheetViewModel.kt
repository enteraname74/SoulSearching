package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.domain.model.Scope
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.model.player.PlayedListScope
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.soulsearching.domain.usecase.collection.CommonCollectionUseCase
import com.github.enteraname74.soulsearching.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiPlaylistDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveFromCollectionDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeletePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class PlaylistBottomSheetViewModel(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonCollectionUseCase: CommonCollectionUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: PlaylistBottomSheetNavScope,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    settings: SoulSearchingSettings,
    params: PlaylistBottomSheetDestination,
) : ViewModel() {
    private val playlistIds: List<Uuid> = params.playlistIds
    private val collectionId: Uuid? = params.collectionId

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<PlaylistBottomSheetState> = combine(
        commonPlaylistUseCase.getFromIds(playlistIds),
        playbackManager.playedList,
        dialogState,
        settings.getFlowOn(
            settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
        ),
        settings.getFlowOn(SoulSearchingSettingsKeys.MainPage.ARE_COLLECTIONS_SHOWN),
        hasValidCloudInformationUseCase(),
        playbackManager.currentScope,
    ) { data ->
        val playlists = data[0] as List<PlaylistWithMusics>
        val playedList = data[1] as List<Music>
        val dialogState = data[2] as SoulDialog?
        val isQuickAccessShown = data[3] as Boolean
        val areCollectionsShown = data[4] as Boolean
        val hasValidCloudInformation = data[5] as Boolean
        val playedListScope = data[6] as PlayedListScope?

        PlaylistBottomSheetState(
            playlists = playlists,
            bottomSheetTopInformation = buildTopInformation(playlists),
            rowSpecs = buildRowSpecs(
                playlists = playlists,
                isQuickAccessShown = isQuickAccessShown,
                areCollectionsShown = areCollectionsShown,
                hasValidCloudInformation = hasValidCloudInformation,
                playedList = playedList,
                playedListScope = playedListScope,
            ),
            dialogState = dialogState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PlaylistBottomSheetState(),
    )

    private fun buildRowSpecs(
        playlists: List<PlaylistWithMusics>,
        playedList: List<Music>,
        isQuickAccessShown: Boolean,
        areCollectionsShown: Boolean,
        hasValidCloudInformation: Boolean,
        playedListScope: PlayedListScope?,
    ): List<BottomSheetRowSpec> = buildList {
        if (playlists.isEmpty()) return@buildList

        val editEnabled: Boolean = playlists.size == 1
        val hasUserMusics: Boolean = playlists.any { playlist ->
            playlist.musics.any { it.scope == Scope.User }
        }
        val hasMusics: Boolean = playlists.any { it.musics.isNotEmpty() }
        val showDelete: Boolean = if (playlists.size == 1) {
            !playlists.first().playlist.isFavorite
        } else {
            true
        }

        if (isQuickAccessShown) {
            val isInQuickAccess: Boolean = if (playlists.size == 1) {
                playlists.first().playlist.isInQuickAccess
            } else {
                playlists.all { it.playlist.isInQuickAccess }
            }

            add(
                BottomSheetRowSpec.quickAccess(
                    onClick = { handleQuickAccess(!isInQuickAccess) },
                    isInQuickAccess = isInQuickAccess,
                )
            )
        }

        if (editEnabled) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_edit_filled,
                    title = strings.modifyPlaylist,
                    onClick = ::toModifyPlaylist,
                )
            )
        }

        if (hasUserMusics) {
            add(BottomSheetRowSpec.addToPlaylist(::addToPlaylists))
        }

        if (areCollectionsShown) {
            add(BottomSheetRowSpec.addToCollection(::addToCollections))
        }

        if (hasMusics && playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.playNext(::playNext))
        }

        if (hasMusics) {
            add(BottomSheetRowSpec.addToQueue(::addToQueue))
        }

        if (hasValidCloudInformation && hasUserMusics && playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.startSharedPlayedList(::startSharedPlayedList))
        }

        if (playedList.isNotEmpty()) {
            add(
                BottomSheetRowSpec.removeFromPlayedList(::removeFromPlayedList),
            )
        }

        if (collectionId != null && playlists.size == 1) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_delete_filled,
                    title = strings.removeFromCollection,
                    onClick = ::showRemoveFromCollectionDialog,
                )
            )
        }

        if (showDelete) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_delete_filled,
                    title = if (playlists.size == 1) {
                        strings.deletePlaylist
                    } else {
                        strings.deleteSelectedPlaylists
                    },
                    onClick = ::showDeleteDialog,
                )
            )
        }
    }

    private fun toModifyPlaylist() {
        playlistIds.firstOrNull()?.let {
            viewModelScope.launch {
                multiSelectionManager.clearMultiSelection()
                navScope.toModifyPlaylist(it)
            }
        }
    }

    private fun buildTopInformation(playlists: List<PlaylistWithMusics>): BottomSheetTopInformation =
        if (playlists.size == 1) {
            val playlist = playlists.first()
            BottomSheetTopInformation(
                title = playlist.playlist.name,
                subTitle = strings.musics(total = playlist.musics.filter { !it.isHidden }.size),
                cover = playlist.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = playlists.size),
                cover = null,
            )
        }

    private fun showDeleteDialog() {
        dialogState.value = if (state.value.playlists.size == 1) {
            DeletePlaylistDialog(
                onDelete = ::deletePlaylists,
                onClose = { dialogState.value = null },
            )
        } else {
            DeleteMultiPlaylistDialog(
                onDelete = ::deletePlaylists,
                onClose = { dialogState.value = null },
            )
        }
    }

    private fun showRemoveFromCollectionDialog() {
        dialogState.value = RemoveFromCollectionDialog(
            title = strings.removePlaylistFromCollectionTitle,
            text = strings.removePlaylistFromCollectionText,
            onConfirm = ::removeFromCollection,
            onClose = { dialogState.value = null },
        )
    }

    private fun removeFromCollection() {
        val collectionId = collectionId ?: return
        val playlistId = playlistIds.firstOrNull() ?: return
        viewModelScope.launch {
            loadingManager.startLoading()
            val result = commonCollectionUseCase.removePlaylist(collectionId, playlistId)
            loadingManager.stopLoading()
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                dialogState.value = null
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun deletePlaylists() {
        viewModelScope.launch {
            dialogState.value = null
            loadingManager.withLoading {
                val result = commonPlaylistUseCase.deleteAll(playlistIds)
                feedbackPopUpManager.showErrorIfAny(result)
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonPlaylistUseCase.upsertAll(
                    playlists = state.value.playlists.map {
                        it.playlist.copy(
                            isInQuickAccess = newValue,
                        )
                    }
                )
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun playNext() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val musics: List<Music> =
                state.value.playlists
                    .flatMap { it.musics }
                    .distinctBy { it.musicId }

            val result = playbackManager.addMultipleMusicsToPlayNext(
                musics = musics,
            )
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
            val musics: List<Music> =
                state.value.playlists
                    .flatMap { it.musics }
                    .distinctBy { it.musicId }

            val result = playbackManager.addMultipleMusicsToQueue(
                musics = musics,
            )
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun addToPlaylists() {
        val musicIds: List<Uuid> =
            state.value.playlists
                .flatMap { it.musics }
                .distinctBy { it.musicId }
                .map { it.musicId }

        navScope.toAddToPlaylists(musicIds)
    }

    private fun addToCollections() {
        navScope.toAddToCollections(playlistIds)
    }

    private fun removeFromPlayedList() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val musicIds: List<Uuid> =
                state.value.playlists
                    .flatMap { it.musics }
                    .distinctBy { it.musicId }
                    .map { it.musicId }

            val result = playbackManager.removeSongsFromPlayedList(
                musicIds = musicIds,
            )
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
            val musicIds: List<Uuid> =
                state.value.playlists
                    .flatMap { it.musics }
                    .filter { it.scope != Scope.SharedPlayedList }
                    .distinctBy { it.musicId }
                    .map { it.musicId }

            val result = playbackManager.startSharedList(
                musicIds = musicIds,
            )
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
