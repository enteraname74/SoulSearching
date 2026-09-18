package com.github.enteraname74.soulsearching.feature.managefolders

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.SaveInitialFetchedMusicsUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class ManageFoldersViewHolder(
    private val commonFolderUseCase: CommonFolderUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val musicFetcher: MusicFetcher,
    private val mode: ManageFoldersDestination.Mode,
    private val saveInitialFetchedMusicsUseCase: SaveInitialFetchedMusicsUseCase,
    workDispatcher: WorkDispatcher,
) : SoulViewModelHolderV2<ManageFoldersNavScope, ManageFoldersState>() {
    private val workScope = CoroutineScope(workDispatcher.dispatcher)
    private val folderPathSelectionState: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(mapOf())

    override fun getInitialState(): ManageFoldersState =
        ManageFoldersState(
            folders = emptyList(),
            navigateBack = { navigate { navigateBack() } }.takeIf { mode == ManageFoldersDestination.Mode.Settings },
            onDone = ::saveSelection,
            setStatus = ::setFolderSelectionStatus,
        )

    init {
        viewModelScope.launch {
            combine(
                commonFolderUseCase.getAll(),
                folderPathSelectionState,
            ) { folders, pathSelectionState ->
                folders
                    .sortedBy { it.name }
                    .map { folder ->
                        folder.copy(
                            isSelected = pathSelectionState[folder.folderPath] ?: folder.isSelected,
                        )
                    }
            }.collectLatest { folders ->
                updateState { copy(folders = folders) }
            }
        }
    }

    private fun setFolderSelectionStatus(
        folder: Folder,
        isSelected: Boolean,
    ) {
        folderPathSelectionState.value += folder.folderPath to isSelected
    }

    private suspend fun handleInitialFetchSelection() {
        commonFolderUseCase.setAll(folders = currentState.folders)

        // Remove musics from unselected folders
        val unselectedFolderPaths = currentState.folders.filter { !it.isSelected }.map { it.folderPath }
        val filtered = musicFetcher.optimizedCachedData.musicsByPath.filter { (_, value) ->
            !unselectedFolderPaths.contains(value.folder)
        }
        val newMap = HashMap<String, Music>()
        newMap.putAll(filtered)
        musicFetcher.optimizedCachedData.musicsByPath = newMap

        // Navigate to next step (either app or multiple artists
        val multipleArtistManager = FetchAllMultipleArtistManagerImpl(
            optimizedCachedData = musicFetcher.optimizedCachedData,
        )
        if (multipleArtistManager.doDataHaveMultipleArtists()) {
            navigate { navigateToMultipleArtists() }
        } else {
            // We must save the songs here
            saveInitialFetchedMusicsUseCase(
                musics = musicFetcher.optimizedCachedData.musicsByPath.values.toList(),
            )
            navigate { navigateToApp() }
        }
    }

    private suspend fun handleSettingsSelection() {
        commonFolderUseCase.upsertAll(allFolders = currentState.folders)

        val musicIds: List<Uuid> = commonMusicUseCase.getAllIdsFromUnselectedFolders()
        deleteMusicUseCase.fromUnselectedFolders(ids = musicIds)
        // TODO SHARED PLAYED LIST: Should we show the error if the call doesn't work?
        playbackManager.removeSongsFromPlayedList(musicIds = musicIds)
        feedbackPopUpManager.showFeedback(
            feedback = strings.savedChanges,
        )
        navigate { navigateBack() }
    }

    private fun saveSelection() {
        loadingManager.withLoadingOnScope(workScope) {
            when (mode) {
                ManageFoldersDestination.Mode.InitialFetch -> handleInitialFetchSelection()
                ManageFoldersDestination.Mode.Settings -> handleSettingsSelection()
            }
        }
    }

    @Composable
    override fun Content(state: ManageFoldersState) {
        ManageFoldersScreen(state)
    }
}
