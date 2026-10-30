package com.github.enteraname74.soulsearching.feature.search.all

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.composables.bottomsheets.album.AlbumBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.artist.ArtistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.collection.CollectionBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheetDestination
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.MusicListDetailId
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.soulsearching.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.soulsearching.domain.usecase.collection.CommonCollectionUseCase
import com.github.enteraname74.soulsearching.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.feature.musiclistdetail.MusicListDetailDestination
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class SearchAllViewHolder(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonCollectionUseCase: CommonCollectionUseCase,
    private val focusRequester: FocusRequester,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
) : SoulViewModelHolderV2<SearchAllNavScope, SearchAllState>() {
    private val search: MutableStateFlow<String> = MutableStateFlow("")

    init {
        viewModelScope.launch {
            search.flatMapLatest { search ->
                updateState { copy(search = search) }

                // For smooth UI
                if (search.isNotEmpty()) {
                    delay(300.milliseconds)
                    combine(
                        commonMusicUseCase.searchAll(search),
                        commonArtistUseCase.searchAll(search),
                        commonAlbumUseCase.searchAll(search),
                        commonPlaylistUseCase.searchAll(search),
                        commonCollectionUseCase.searchAll(search),
                    ) { musics, artists, albums, playlists, collections ->
                        currentState.copy(
                            musics = musics,
                            albums = albums,
                            playlists = playlists,
                            artists = artists,
                            collections = collections,
                        )
                    }
                } else {
                    flowOf(
                        currentState.copy(
                            musics = emptyList(),
                            albums = emptyList(),
                            playlists = emptyList(),
                            artists = emptyList(),
                            collections = emptyList(),
                        )
                    )
                }
            }.collectLatest { state ->
                updateState { state }
            }
        }
    }

    override fun getInitialState(): SearchAllState = SearchAllState(
        search = "",
        musics = emptyList(),
        albums = emptyList(),
        playlists = emptyList(),
        artists = emptyList(),
        collections = emptyList(),
        selectedSearchableElements = SearchAllState.SearchableElement.entries.toSet(),
        focusRequester = focusRequester,
        navigateBack = { navigate { navigateBack() } },
        onSearch = {
            search.value = it
        },
        showMusicBottomSheet = {
            navigate {
                toDestination(MusicBottomSheetDestination(listOf(it)))
            }
        },
        showAlbumBottomSheet = {
            navigate {
                toDestination(AlbumBottomSheetDestination(listOf(it)))
            }
        },
        showPlaylistBottomSheet = {
            navigate {
                toDestination(PlaylistBottomSheetDestination(listOf(it)))
            }
        },
        showArtistBottomSheet = {
            navigate {
                toDestination(ArtistBottomSheetDestination(listOf(it)))
            }
        },
        showCollectionBottomSheet = {
            navigate {
                toDestination(CollectionBottomSheetDestination(listOf(it)))
            }
        },
        onMusicClick = ::playSearchList,
        navigateToPlaylist = {
            navigate {
                toDestination(MusicListDetailDestination(MusicListDetailId.Playlist(it)))
            }
        },
        navigateToCollection = {
            navigate {
                toDestination(MusicListDetailDestination(MusicListDetailId.Collection(it)))
            }
        },
        navigateToArtist = {
            navigate {
                toDestination(MusicListDetailDestination(MusicListDetailId.Artist(it)))
            }
        },
        navigateToAlbum = {
            navigate {
                toDestination(MusicListDetailDestination(MusicListDetailId.Album(it)))
            }
        },
        onSearchInAllElements = {
            updateState {
                copy(
                    selectedSearchableElements = SearchAllState.SearchableElement.entries.toSet(),
                )
            }
        },
        onToggleSearchableElement = { element ->
            updateState {
                val updatedList = when {
                    SearchAllState.SearchableElement.entries.all { it in selectedSearchableElements } -> setOf(element)
                    element in selectedSearchableElements -> selectedSearchableElements - element
                    else -> selectedSearchableElements + element
                }

                copy(
                    selectedSearchableElements = updatedList,
                )
            }
        }
    )

    private fun playSearchList(
        music: Music
    ) {
        viewModelScope.launch {
            playbackManager.setCurrentPlaylistAndMusic(
                music = music,
                musicList = currentState.musics,
                playlistId = null,
                isMainPlaylist = false,
                isForcingNewPlaylist = true
            )
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    @Composable
    override fun Content(state: SearchAllState) {
        SearchAllScreen(state)
    }
}