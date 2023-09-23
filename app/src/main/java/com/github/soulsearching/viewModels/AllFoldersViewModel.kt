package com.github.soulsearching.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.FolderDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.model.Folder
import com.github.soulsearching.states.FolderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllFoldersViewModel @Inject constructor(
    private val folderDao: FolderDao,
    private val musicDao: MusicDao
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

    fun updateFolderState(folder: Folder) {
        CoroutineScope(Dispatchers.IO).launch {
            musicDao.updateMusicsHiddenState(folder.folderPath, folder.isSelected)
            folderDao.insertFolder(
                Folder(
                    folderPath = folder.folderPath,
                    isSelected = !folder.isSelected
                )
            )
        }
    }
}