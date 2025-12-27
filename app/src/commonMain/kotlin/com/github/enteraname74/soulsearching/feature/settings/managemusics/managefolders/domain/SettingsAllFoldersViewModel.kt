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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SettingsAllFoldersViewModel(
    private val commonFolderUseCase: CommonFolderUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
): ViewModel() {
    private val isFetchingFolders: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val folders: MutableStateFlow<List<Folder>> = MutableStateFlow(emptyList())
    val state: StateFlow<FolderState> = combine(
        isFetchingFolders,
        folders,
    ) { isFetchingFolders, folders ->
        when {
            isFetchingFolders -> FolderState.Fetching
            else -> FolderState.Data(folders.sortedBy { it.folderPath })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FolderState.Fetching,
    )

    init {
        updateFolders()
    }

    private fun updateFolders() {
        CoroutineScope(Dispatchers.IO).launch {
            isFetchingFolders.value = true
            val folders: List<Folder> = commonFolderUseCase.getAll().first()
            this@SettingsAllFoldersViewModel.folders.value = folders
            isFetchingFolders.value = false
        }
    }

    fun setFolderSelectionStatus(
        folder: Folder,
        isSelected: Boolean,
    ) {
        if (state.value !is FolderState.Data) {
            return
        }
        folders.update {
            it.map { savedFolder ->
                if (savedFolder.folderPath == folder.folderPath) {
                    savedFolder.copy(
                        isSelected = isSelected
                    )
                } else {
                    savedFolder.copy()
                }
            }
        }
    }

    fun saveSelection() {
        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                val folders = folders.value

                commonFolderUseCase.upsertAll(
                    allFolders = folders
                )
                val musicIds: List<UUID> = commonMusicUseCase.getAllIdsFromUnselectedFolders()
                commonMusicUseCase.deleteAllFromUnselectedFolders()
                playbackManager.removeSongsFromPlayedPlaylist(musicIds = musicIds)
            }
            updateFolders()
        }
    }
}