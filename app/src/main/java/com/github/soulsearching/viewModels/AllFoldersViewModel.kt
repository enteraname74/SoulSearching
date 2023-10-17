package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.classes.enumsAndTypes.FolderStateType
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Folder
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.states.FolderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllFoldersViewModel @Inject constructor(
    private val folderDao: FolderDao,
    private val musicDao: MusicDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao,
) : ViewModel() {
    private val _folders = folderDao.getAllFolders().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ArrayList()
    )

    private val _state = MutableStateFlow(FolderState())

    val state = combine(
        _state,
        _folders,
    ) { state, folders ->
        state.copy(
            folders = folders as ArrayList<Folder>
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FolderState()
    )

    fun onFolderEvent(event: FolderEvent) {
        when (event) {
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
            FolderEvent.SaveSelection -> {
                CoroutineScope(Dispatchers.IO).launch {
                    _state.update {
                        it.copy(
                            state = FolderStateType.SAVING_SELECTION
                        )
                    }
                    _state.value.folders.forEach { folder ->

                        folderDao.insertFolder(
                            Folder(
                                folderPath = folder.folderPath,
                                isSelected = !folder.isSelected
                            )
                        )
                        
                        if (!folder.isSelected) {
                            val musicsFromFolder = musicDao.getMusicsFromFolder(folder.folderPath)
                            musicsFromFolder.forEach { music ->
                                Utils.removeMusicFromApp(
                                    musicDao = musicDao,
                                    albumDao = albumDao,
                                    artistDao = artistDao,
                                    albumArtistDao = albumArtistDao,
                                    musicAlbumDao = musicAlbumDao,
                                    musicArtistDao = musicArtistDao,
                                    musicToRemove = music
                                )
                            }
                        }
                    }
                    _state.update {
                        it.copy(
                            state = FolderStateType.WAITING_FOR_USER_ACTION
                        )
                    }
                }
            }
        }
    }
}