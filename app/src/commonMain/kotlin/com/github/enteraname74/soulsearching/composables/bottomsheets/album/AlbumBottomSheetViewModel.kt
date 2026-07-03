package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteAlbumDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiAlbumDialog
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

class AlbumBottomSheetViewModel(
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: AlbumBottomSheetNavScope,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    settings: SoulSearchingSettings,
    params:  AlbumBottomSheetDestination,
): ViewModel() {
    private val albumIds: List<Uuid> = params.albumIds

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<AlbumBottomSheetState> = combine(
        commonAlbumUseCase.getFromIds(albumIds),
        playbackManager.playedList,
        dialogState,
        settings.getFlowOn(
            settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
        ),
        hasValidCloudInformationUseCase(),
        playbackManager.currentScope,
    ) { data ->
        val albums = data[0] as List<AlbumWithMusics>
        val playedList = data[1] as List<Music>
        val dialogState = data[2] as SoulDialog?
        val isQuickAccessShown = data[3] as Boolean
        val hasValidCloudInformation = data[4] as Boolean
        val playedListScope = data[5] as PlayedListScope?

        AlbumBottomSheetState(
            albums = albums,
            bottomSheetTopInformation = buildTopInformation(albums),
            rowSpecs = buildRowSpecs(
                albums = albums,
                isQuickAccessShown = isQuickAccessShown,
                hasValidCloudInformation = hasValidCloudInformation,
                playedList = playedList,
                playedListScope = playedListScope,
            ),
            dialogState = dialogState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AlbumBottomSheetState(),
    )

    private fun buildRowSpecs(
        albums: List<AlbumWithMusics>,
        playedList: List<Music>,
        isQuickAccessShown: Boolean,
        hasValidCloudInformation: Boolean,
        playedListScope: PlayedListScope?,
    ) : List<BottomSheetRowSpec> = buildList {
        if (albums.isEmpty()) return@buildList

        val editEnabled: Boolean = albums.size == 1
        val hasUserMusics: Boolean = albums.any { album ->
            album.musics.any { it.scope == Scope.User }
        }

        if (isQuickAccessShown) {
            val isInQuickAccess: Boolean = if (albums.size == 1) {
                albums.first().album.isInQuickAccess
            } else {
                albums.all { it.album.isInQuickAccess }
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
                    title = strings.modifyAlbum,
                    onClick = ::toModifyAlbum,
                )
            )
        }

        if (playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.playNext(::playNext))
        }

        add(BottomSheetRowSpec.addToQueue(::addToQueue))

        if (hasValidCloudInformation && hasUserMusics) {
            add(BottomSheetRowSpec.startSharedPlayedList(::startSharedPlayedList))
        }

        if (playedList.isNotEmpty()) {
            add(
                BottomSheetRowSpec.removeFromPlayedList(::removeFromPlayedList),
            )
        }

        add(
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_delete_filled,
                title = if (albums.size == 1) {
                    strings.deleteAlbum
                } else {
                    strings.deleteSelectedAlbums
                },
                onClick = ::showDeleteDialog,
            )
        )
    }

    private fun toModifyAlbum() {
        albumIds.firstOrNull()?.let {
            viewModelScope.launch {
                multiSelectionManager.clearMultiSelection()
                navScope.toModifyAlbum(it)
            }
        }
    }

    private fun buildTopInformation(albums: List<AlbumWithMusics>): BottomSheetTopInformation =
        if (albums.size == 1) {
            val album = albums.first()
            BottomSheetTopInformation(
                title = album.album.albumName,
                subTitle = strings.musics(total = album.musics.filter { !it.isHidden }.size),
                cover = album.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = albums.size),
                cover = null,
            )
        }

    private fun showDeleteDialog() {
        dialogState.value = if (state.value.albums.size == 1) {
            DeleteAlbumDialog(
                onDelete = ::deleteAlbums,
                onClose = { dialogState.value = null },
            )
        } else {
            DeleteMultiAlbumDialog(
                onDelete = ::deleteAlbums,
                onClose = { dialogState.value = null },
            )
        }
    }

    private fun deleteAlbums() {
        viewModelScope.launch {
            dialogState.value = null
            loadingManager.withLoading {
                deleteAlbumUseCase(state.value.albums.map { it.album.albumId })
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonAlbumUseCase.upsertAll(
                    albums = state.value.albums.map {
                        it.album.copy(
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
                state.value.albums
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
                state.value.albums
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

    private fun removeFromPlayedList() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val musicIds: List<Uuid> =
                state.value.albums
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
                state.value.albums
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
