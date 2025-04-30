package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.folder.GetAllFoldersUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertFolderUseCase
import com.github.enteraname74.domain.usecase.music.DeleteAllMusicsUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicFromFolderPathUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SettingsAllFoldersViewModel(
    private val getAllFoldersUseCase: GetAllFoldersUseCase,
    private val upsertFolderUseCase: UpsertFolderUseCase,
    private val getAllMusicFromFolderPathUseCase: GetAllMusicFromFolderPathUseCase,
    private val deleteAllMusicsUseCase: DeleteAllMusicsUseCase,
    private val getAllArtistWithMusicsUseCase: GetAllArtistWithMusicsUseCase,
    private val commonAlbumsUseCase: CommonAlbumUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
): ScreenModel {
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

    fun saveSelection() {
        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                folders.value.forEach { folder ->
                    upsertFolderUseCase(
                        Folder(
                            folderPath = folder.folderPath,
                            isSelected = folder.isSelected
                        )
                    )

                    if (!folder.isSelected) {
                        val musicsFromFolder: List<UUID> = getAllMusicFromFolderPathUseCase(folder.folderPath)
                            .first()
                            .map { it.musicId }
                        deleteAllMusicsUseCase(ids = musicsFromFolder)
                        playbackManager.removeSongsFromPlayedPlaylist(musicIds = musicsFromFolder)

                        val albumsToDelete = commonAlbumsUseCase.getAllAlbumsWithMusics()
                            .first()
                            .filter { it.musics.isEmpty() }
                            .map { it.album.albumId }

                        val artistsToDelete = getAllArtistWithMusicsUseCase()
                            .first()
                            .filter { it.musics.isEmpty() }
                            .map { it.artist.artistId }

                        commonAlbumsUseCase.deleteAll(albumsIds = albumsToDelete)
                        commonArtistUseCase.deleteAll(artistsIds = artistsToDelete)
                    }
                }
            }
            updateFolders()
        }
    }
}