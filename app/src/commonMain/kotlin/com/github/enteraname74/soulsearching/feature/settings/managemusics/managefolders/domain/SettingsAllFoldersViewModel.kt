package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SettingsAllFoldersViewModel(
    private val commonFolderUseCase: CommonFolderUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
) : ViewModel() {

    private val workScope = CoroutineScope(Dispatchers.IO)
    private val folderPathSelectionState: MutableStateFlow<Map<String, Boolean>> =
        MutableStateFlow(mapOf())

    val state: StateFlow<FolderState> = combine(
        commonFolderUseCase.getAll(),
        folderPathSelectionState,
    ) { folders, pathSelectionState ->
        FolderState(
            folders
                .sortedBy { it.name }
                .map { folder ->
                    folder.copy(
                        isSelected = pathSelectionState[folder.folderPath] ?: folder.isSelected,
                    )
                }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FolderState(),
    )

    fun setFolderSelectionStatus(
        folder: Folder,
        isSelected: Boolean,
    ) {
        folderPathSelectionState.value += folder.folderPath to isSelected
    }

    fun saveSelection() {
        workScope.launch {
            loadingManager.withLoading {
                commonFolderUseCase.upsertAll(allFolders = state.value.folders)

                val musicIds: List<UUID> = commonMusicUseCase.getAllIdsFromUnselectedFolders()
                commonMusicUseCase.deleteAllFromUnselectedFolders()
                playbackManager.removeSongsFromPlayedPlaylist(musicIds = musicIds)
            }
        }
    }
}