package com.github.enteraname74.soulsearching.feature.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.playback_error_player_error
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.domain.usecase.ShouldInformOfNewReleaseUseCase
import com.github.enteraname74.soulsearching.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.soulsearching.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.soulsearching.domain.usecase.collection.CommonCollectionUseCase
import com.github.enteraname74.soulsearching.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.tabmanager.TabManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainAppViewModel(
    shouldInformOfNewReleaseUseCase: ShouldInformOfNewReleaseUseCase,
    private val tabManager: TabManager,
    private val playerViewManager: PlayerViewManager,
    private val navScope: MainAppNavScope,
    private val playbackManager: PlaybackManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    commonMusicUseCase: CommonMusicUseCase,
    commonPlaylistUseCase: CommonPlaylistUseCase,
    commonAlbumUseCase: CommonAlbumUseCase,
    commonArtistUseCase: CommonArtistUseCase,
    commonCollectionUseCase: CommonCollectionUseCase,
) : ViewModel() {
    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<MainAppState> = combine(
        tabManager.tabs,
        tabManager.currentPage,
        shouldInformOfNewReleaseUseCase(),
        commonMusicUseCase.getTotal(),
        commonMusicUseCase.getTotalMusicFolders(),
        commonPlaylistUseCase.getTotal(),
        commonAlbumUseCase.getTotal(),
        commonArtistUseCase.getTotal(),
        commonCollectionUseCase.getTotal(),
    ) { data ->
        MainAppState(
            navigationRows = buildNavigationRows(
                shouldShowNewVersionPin = data[2] as Boolean,
                currentPage = data[1] as ElementEnum?,
                tabs = data[0] as List<ElementEnum>,
                totalMusics = data[3] as Int,
                totalPlaylists = data[5] as Int,
                totalAlbums = data[6] as Int,
                totalArtists = data[7] as Int,
                totalFolders = data[4] as Int,
                totalCollections = data[8] as Int,
            )
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainAppState(),
        started = SharingStarted.Eagerly,
    )

    init {
        viewModelScope.launch {
            playbackManager.playbackError.filterNotNull().collectLatest {
                feedbackPopUpManager.showFeedback(CoreRes.string.playback_error_player_error.toTextSpec())
                playbackManager.consumeError()
            }
        }
    }

    private fun buildNavigationRows(
        shouldShowNewVersionPin: Boolean,
        currentPage: ElementEnum?,
        tabs: List<ElementEnum>,
        totalMusics: Int,
        totalPlaylists: Int,
        totalAlbums: Int,
        totalArtists: Int,
        totalFolders: Int,
        totalCollections: Int,
    ): List<NavigationRowSpec> = buildList {
        add(
            NavigationRowSpec.Search(
                onClick = {
                    playerViewManager.minimiseIfPossible()
                    navScope.toSearch()
                },
            )
        )
        add(
            NavigationRowSpec.Settings(
                onClick = {
                    playerViewManager.minimiseIfPossible()
                    navScope.toSettings()
                },
                isBadged = shouldShowNewVersionPin,
            )
        )
        tabs.forEachIndexed { index, tab ->

            val pageCheck: Boolean = (currentPage == null && index == 0) || (currentPage == tab)

            val totalForUi = when (tab) {
                ElementEnum.QUICK_ACCESS -> null
                ElementEnum.PLAYLISTS -> totalPlaylists
                ElementEnum.COLLECTIONS -> totalCollections
                ElementEnum.ALBUMS -> totalAlbums
                ElementEnum.ARTISTS -> totalArtists
                ElementEnum.MUSICS -> totalMusics
                ElementEnum.FOLDERS -> totalFolders
            }?.takeIf { it > 0 }

            add(
                NavigationRowSpec.MainTabElement(
                    element = tab,
                    isCurrentPage = pageCheck,
                    trailingText = totalForUi?.let { SoulTextSpec.Raw(it.toString()) },
                    onClick = {
                        playerViewManager.minimiseIfPossible()
                        navScope.toMainPageDestinationIfNeeded()
                        tabManager.setCurrentPage(tab)
                    },
                )
            )
        }
    }
}
