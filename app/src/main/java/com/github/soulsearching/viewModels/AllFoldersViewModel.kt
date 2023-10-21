package com.github.soulsearching.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.classes.enumsAndTypes.FolderStateType
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Folder
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.states.FolderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val _state = MutableStateFlow(FolderState())
    val state = _state.asStateFlow()

    fun onFolderEvent(event: FolderEvent) {
        when (event) {
            FolderEvent.FetchFolders -> {
                if (_state.value.state == FolderStateType.SAVING_SELECTION) {
                    return
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val folders = folderDao.getAllFoldersSimple()
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

                        folderDao.insertFolder(
                            Folder(
                                folderPath = folder.folderPath,
                                isSelected = folder.isSelected
                            )
                        )

                        if (!folder.isSelected) {
                            val musicsFromFolder = musicDao.getMusicsFromFolder(folder.folderPath)
                            var count = 0
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
                                count++
                                Log.d("FOLDER VM", "UPDATE COUNT : ${(count * 1F) / musicsFromFolder.size}")
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