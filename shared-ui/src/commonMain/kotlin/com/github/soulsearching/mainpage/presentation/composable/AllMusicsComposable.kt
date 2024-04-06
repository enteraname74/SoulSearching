package com.github.soulsearching.mainpage.presentation.composable

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.events.MusicEvent
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.mainpage.domain.state.MusicState
import com.github.soulsearching.strings.strings
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.mainpage.domain.model.SortDirection
import com.github.soulsearching.mainpage.domain.model.SortType
import com.github.soulsearching.domain.viewmodel.AllMusicsViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllMusicsComposable(
    allMusicsViewModel: AllMusicsViewModel,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    musicState: MusicState,
    sortByName: () -> Unit = {},
    sortByDateAction: () -> Unit = {},
    sortByMostListenedAction: () -> Unit = {},
    setSortDirectionAction: () -> Unit = {},
    sortType: Int = SortType.NAME,
    sortDirection: Int = SortDirection.DESC,
    isUsingSort: Boolean = true,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    savePlayerMusicListMethod: (List<UUID>) -> Unit,
    playbackManager: PlaybackManager = injectElement()
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Constants.Spacing.large)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubMenuComposable(
                title = strings.musics,
                sortByDateAction = sortByDateAction,
                sortByMostListenedAction = sortByMostListenedAction,
                sortByName = sortByName,
                setSortDirectionAction = setSortDirectionAction,
                sortType = sortType,
                sortDirection = sortDirection,
                isUsingSort = isUsingSort
            )
        }
        if (musicState.musics.isNotEmpty()) {
            LazyColumn {
                items(items = musicState.musics) { elt ->
                    MusicItemComposable(
                        music = elt,
                        onClick = { music ->
                            coroutineScope.launch {
                                playerDraggableState.animateTo(
                                    BottomSheetStates.EXPANDED,
                                    tween(Constants.AnimationDuration.normal)
                                )
                            }.invokeOnCompletion {
                                if (!playbackManager.isSamePlaylist(
                                        true,
                                        null
                                    )
                                ) {
                                    savePlayerMusicListMethod(
                                        musicState.musics.map { it.musicId }
                                    )
                                }
                                playbackManager.setCurrentPlaylistAndMusic(
                                    music = music,
                                    playlist = musicState.musics,
                                    isMainPlaylist = true,
                                    playlistId = null,
                                )
                            }
                        },
                        onLongClick = {
                            coroutineScope.launch {
                                allMusicsViewModel.handler.onMusicEvent(
                                    MusicEvent.SetSelectedMusic(
                                        elt
                                    )
                                )
                                allMusicsViewModel.handler.onMusicEvent(
                                    MusicEvent.BottomSheet(
                                        isShown = true
                                    )
                                )
                            }
                        },
                        musicCover = retrieveCoverMethod(elt.coverId),
                        isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(elt.musicId)
                    )
                }
                item {
                    PlayerSpacer()
                }
            }
        } else {
            NoElementView()
        }
    }
}