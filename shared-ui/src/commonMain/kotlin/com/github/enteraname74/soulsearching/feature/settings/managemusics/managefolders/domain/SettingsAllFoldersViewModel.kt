package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.usecase.folder.GetAllFoldersUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertFolderUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicFromFolderPathUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsAllFoldersViewModel(
    private val getAllFoldersUseCase: GetAllFoldersUseCase,
    private val upsertFolderUseCase: UpsertFolderUseCase,
    private val getAllMusicFromFolderPathUseCase: GetAllMusicFromFolderPathUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
): ScreenModel {
    private val isSavingFolders: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isFetchingFolders: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val folders: MutableStateFlow<List<Folder>> = MutableStateFlow(emptyList())
    val state: StateFlow<FolderState> = combine(
        isSavingFolders,
        isFetchingFolders,
        folders,
    ) { isSavingFolders, isFetchingFolders, folders ->
        when {
            isSavingFolders -> FolderState.Saving
            isFetchingFolders -> FolderState.Fetching
            else -> FolderState.Data(folders)
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FolderState.Fetching,
    )

    init {
        updateFolders()
    }

    private fun updateFolders() {
        CoroutineScope(Dispatchers.IO).launch {
            isFetchingFolders.value = true
            val folders: List<Folder> = getAllFoldersUseCase().first()
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

    fun saveSelection(
        updateProgress: (Float) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            isSavingFolders.value = true
            folders.value.forEach { folder ->
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
                        updateProgress((count * 1F) / musicsFromFolder.size)
                    }
                }
            }
            isSavingFolders.value = false
            updateFolders()
        }
    }
}