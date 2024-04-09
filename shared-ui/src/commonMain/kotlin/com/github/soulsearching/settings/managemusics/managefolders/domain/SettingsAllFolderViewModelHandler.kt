package com.github.soulsearching.settings.managemusics.managefolders.domain

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import com.github.soulsearching.settings.managemusics.managefolders.domain.model.FolderStateType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Handler for managing the AllFolderViewModel.
 */
open class SettingsAllFolderViewModelHandler(
    private val folderRepository: FolderRepository,
    private val musicRepository: MusicRepository,
): ViewModelHandler {
    private val _state = MutableStateFlow(FolderState())
    val state = _state.asStateFlow()

    /**
     * Manage folder events.
     */
    fun onFolderEvent(event: FolderEvent) {
        when (event) {
            FolderEvent.FetchFolders -> {
                if (_state.value.state == FolderStateType.SAVING_SELECTION) {
                    return
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val folders = folderRepository.getAllFolders()
                    _state.update {
                        it.copy(
                            folders = folders as ArrayList<Folder>,
                            state = FolderStateType.WAITING_FOR_USER_ACTION
                        )
                    }
                }
            }
            is FolderEvent.SetSelectedFolder -> {
                _state.update {
                    it.copy(
                        folders = it.folders.map { folder ->
                            if (folder.folderPath == event.folder.folderPath) {
                                folder.copy(
                                    isSelected = event.isSelected
                                )
                            } else {
                                folder.copy()
                            }
                        } as ArrayList<Folder>
                    )
                }
            }
            is FolderEvent.SetState -> {
                _state.update {
                    it.copy(
                        state = event.newState
                    )
                }
            }
            is FolderEvent.SaveSelection -> {
                CoroutineScope(Dispatchers.IO).launch {
                    _state.update {
                        it.copy(
                            state = FolderStateType.SAVING_SELECTION
                        )
                    }
                    _state.value.folders.forEach { folder ->

                        folderRepository.insertFolder(
                            Folder(
                                folderPath = folder.folderPath,
                                isSelected = folder.isSelected
                            )
                        )

                        if (!folder.isSelected) {
                            val musicsFromFolder = runBlocking {
                                musicRepository.getMusicsFromFolder(folder.folderPath)
                            }
                            var count = 0
                            musicsFromFolder.forEach { music ->
                                musicRepository.delete(
                                    musicId = music.musicId
                                )
                                count++
                                event.updateProgress((count * 1F) / musicsFromFolder.size)
                            }
                        }
                    }
                    onFolderEvent(FolderEvent.SetState(FolderStateType.FETCHING_FOLDERS))
                    onFolderEvent(FolderEvent.FetchFolders)
                }
            }
        }
    }
}