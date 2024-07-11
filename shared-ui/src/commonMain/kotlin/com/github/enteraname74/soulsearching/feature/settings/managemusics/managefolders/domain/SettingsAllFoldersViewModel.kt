package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.usecase.folder.GetAllFoldersUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertFolderUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicFromFolderPathUseCase
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.model.FolderStateType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsAllFoldersViewModel(
    private val getAllFoldersUseCase: GetAllFoldersUseCase,
    private val upsertFolderUseCase: UpsertFolderUseCase,
    private val getAllMusicFromFolderPathUseCase: GetAllMusicFromFolderPathUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
): ScreenModel {
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
                    val folders: List<Folder> = getAllFoldersUseCase().first()
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

                        upsertFolderUseCase(
                            Folder(
                                folderPath = folder.folderPath,
                                isSelected = folder.isSelected
                            )
                        )

                        if (!folder.isSelected) {
                            val musicsFromFolder = runBlocking {
                                getAllMusicFromFolderPathUseCase(folder.folderPath).first()
                            }
                            var count = 0
                            musicsFromFolder.forEach { music ->
                                deleteMusicUseCase(
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