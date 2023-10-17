package com.github.soulsearching.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.MusicUtils
import com.github.soulsearching.database.dao.FolderDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.states.AddMusicsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMusicsViewModel @Inject constructor(
    private val folderDao: FolderDao,
    private val musicDao: MusicDao
) : ViewModel() {
    private var _state = MutableStateFlow(AddMusicsState())
    val state = _state.asStateFlow()

    fun onAddMusicEvent(event: AddMusicsEvent) {
        when (event) {
            AddMusicsEvent.ResetState -> {
                _state.update {
                    AddMusicsState()
                }
            }
            is AddMusicsEvent.AddFetchedMusics -> {
                _state.update {
                    it.copy(
                        fetchedMusics = event.musics
                    )
                }
            }
            is AddMusicsEvent.SetFetchingState -> {
                _state.update {
                    it.copy(
                        isFetchingMusics = event.isFetching
                    )
                }
            }
        }
    }

    fun fetchNewMusics(
        context: Context,
        updateProgressBar: (Float) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val hiddenFoldersPaths = folderDao.getAllHiddenFoldersPaths()
            val allMusics = musicDao.getAllMusicsPaths()

            val newMusics = MusicUtils.fetchNewMusics(
                context,
                updateProgressBar,
                allMusics,
                hiddenFoldersPaths
            )
            onAddMusicEvent(AddMusicsEvent.AddFetchedMusics(newMusics))
            onAddMusicEvent(AddMusicsEvent.SetFetchingState(false))
        }
    }
}