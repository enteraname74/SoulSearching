package com.github.enteraname74.soulsearching.composables.bottomsheets.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMusicsDialog
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlin.uuid.Uuid

class FolderBottomSheetViewModel(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonFolderUseCase: CommonFolderUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: FolderBottomSheetNavScope,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    params: FolderBottomSheetDestination,
) : ViewModel() {
    private val folderPaths: List<String> = params.folderPaths

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    private val folders: Flow<List<MusicFolderPreview>> =
        if (folderPaths.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(
                folderPaths.map { commonMusicUseCase.getMusicFolderPreview(it) }
            ) { previews ->
                previews.filterNotNull()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val musics: Flow<List<Music>> = folders.flatMapLatest {
        flow {
            emit(loadSelectedMusics())
        }
    }

    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<FolderBottomSheetState> = combine(
        folders,
        musics,
        playbackManager.playedList,
        dialogState,
        hasValidCloudInformationUseCase(),
        playbackManager.currentScope,
    ) { data ->
        val folders = data[0] as List<MusicFolderPreview>
        val musics = data[1] as List<Music>
        val playedList = data[2] as List<Music>
        val dialogState = data[3] as SoulDialog?
        val hasValidCloudInformation = data[4] as Boolean
        val playedListScope = data[5] as PlayedListScope?

        FolderBottomSheetState(
            folders = folders,
            musics = musics,
            bottomSheetTopInformation = buildTopInformation(folders),
            rowSpecs = buildRowSpecs(
                folders = folders,
                musics = musics,
                playedList = playedList,
                hasValidCloudInformation = hasValidCloudInformation,
                playedListScope = playedListScope,
            ),
            dialogState = dialogState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FolderBottomSheetState(),
    )

    private suspend fun loadSelectedMusics(): List<Music> =
        folderPaths
            .flatMap { commonMusicUseCase.getAllMusicFromFolder(it) }
            .distinctBy { it.musicId }

    private fun buildRowSpecs(
        folders: List<MusicFolderPreview>,
        musics: List<Music>,
        playedList: List<Music>,
        hasValidCloudInformation: Boolean,
        playedListScope: PlayedListScope?,
    ): List<BottomSheetRowSpec> = buildList {
        if (musics.isEmpty()) return@buildList

        val hasUserMusics: Boolean = musics.any { it.scope == Scope.User }
        val canDelete: Boolean = hasUserMusics && folders.none { it.isCloudyFolder() }

        if (hasUserMusics) {
            add(BottomSheetRowSpec.addToPlaylist(::addToPlaylists))
        }

        if (playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.playNext(::playNext))
        }

        add(BottomSheetRowSpec.addToQueue(::addToQueue))

        if (hasValidCloudInformation && hasUserMusics && playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.startSharedPlayedList(::startSharedPlayedList))
        }

        if (playedList.isNotEmpty()) {
            add(BottomSheetRowSpec.removeFromPlayedList(::removeFromPlayedList))
        }

        if (canDelete) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_delete_filled,
                    title = if (folders.size == 1) {
                        strings.deleteFolderMusics
                    } else {
                        strings.deleteSelectedFoldersMusics
                    },
                    onClick = ::showDeleteDialog,
                )
            )
        }
    }

    private fun buildTopInformation(folders: List<MusicFolderPreview>): BottomSheetTopInformation =
        if (folders.size == 1) {
            val folder = folders.first()
            BottomSheetTopInformation(
                title = folder.name,
                subTitle = strings.musics(total = folder.totalMusics),
                cover = folder.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = folders.size),
                cover = null,
            )
        }

    private fun addToPlaylists() {
        navScope.toAddToPlaylists(state.value.musics.map { it.musicId })
    }

    private fun playNext() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.addMultipleMusicsToPlayNext(
                musics = state.value.musics,
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
            val result = playbackManager.addMultipleMusicsToQueue(
                musics = state.value.musics,
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
            val musicIds: List<Uuid> = state.value.musics.map { it.musicId }

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
                state.value.musics
                    .filter { it.scope != Scope.SharedPlayedList }
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

    private fun showDeleteDialog() {
        val isSingleFolderSelection: Boolean = state.value.folders.size == 1
        dialogState.value = DeleteMusicsDialog(
            onDelete = ::deleteMusics,
            onClose = { dialogState.value = null },
            title = if (isSingleFolderSelection) {
                strings.deleteFolderMusicsDialogTitle
            } else {
                strings.deleteSelectedFoldersMusicsDialogTitle
            },
            text = if (isSingleFolderSelection) {
                strings.deleteFolderMusicsDialogText
            } else {
                strings.deleteSelectedFoldersMusicsDialogText
            },
        )
    }

    private fun deleteMusics() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            dialogState.value = null
            val musicIds: List<Uuid> = state.value.musics.map { it.musicId }
            when (val result = playbackManager.removeSongsFromPlayedList(musicIds)) {
                is SoulResult.Error<*> -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> {
                    deleteMusicUseCase(musicIds = musicIds)
                    commonFolderUseCase.upsertAll(
                        allFolders = folderPaths.map {
                            Folder(
                                folderPath = it,
                                isSelected = false,
                            )
                        }
                    )
                    multiSelectionManager.clearMultiSelection()
                    navScope.navigateBack()
                }
            }
        }
    }

    private fun MusicFolderPreview.isCloudyFolder(): Boolean =
        folder == CLOUDY_FOLDER_NAME || name == CLOUDY_FOLDER_NAME

    private companion object {
        const val CLOUDY_FOLDER_NAME: String = "Cloudy"
    }
}
