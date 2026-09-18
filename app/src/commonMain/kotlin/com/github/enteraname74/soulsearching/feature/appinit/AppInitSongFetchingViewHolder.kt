package com.github.enteraname74.soulsearching.feature.appinit

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppInitSongFetchingViewHolder(
    private val musicFetcher: MusicFetcher,
    private val workDispatcher: WorkDispatcher,
    private val commonFolderUseCase: CommonFolderUseCase,
    private val settings: SoulSearchingSettings,
) : SoulViewModelHolderV2<AppInitSongFetchingNavScope, AppInitSongFetchingState>() {
    override fun getInitialState(): AppInitSongFetchingState =
        AppInitSongFetchingState(
            currentProgression = 0f,
            currentFolder = null,
        )

    @Composable
    override fun Content(state: AppInitSongFetchingState) {
        AppInitSongFetchingScreen(state)
    }

    init {
        fetchSongs()
    }

    fun fetchSongs() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            musicFetcher.fetchMusics(
                updateProgress = { progression, folder ->
                    updateState {
                        AppInitSongFetchingState(
                            currentProgression = progression,
                            currentFolder = folder,
                        )
                    }
                }
            )

            if (musicFetcher.optimizedCachedData.musicsByPath.isEmpty()) {
                settings.set(
                    SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
                    true
                )
                navigate { toApp() }
            } else {
                // Set folders for the manage folder screen
                val musics = musicFetcher.optimizedCachedData.musicsByPath.values
                val folders = musics.groupBy { it.folder }.map { (key, _) ->
                    Folder(
                        folderPath = key,
                        isSelected = true,
                    )
                }
                commonFolderUseCase.setAll(folders)
                navigate { toFoldersSelection() }
            }
        }
    }
}
