package com.github.enteraname74.soulsearching.feature.search

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.search.composable.SearchType
import com.github.enteraname74.soulsearching.coreui.strings.strings
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Suppress("deprecation")
@Composable
fun SearchMusics(
    playerDraggableState: SwipeableState<BottomSheetStates>,
    searchText: String,
    allMusics: List<Music>,
    isMainPlaylist: Boolean,
    focusManager: FocusManager,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    onSelectedMusicForBottomSheet: (Music) -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    playbackManager: PlaybackManager = injectElement()
) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        val foundedMusics = allMusics.filter {
            it.name.lowercase().contains(searchText.lowercase())
                    || it.artist.lowercase().contains(searchText.lowercase())
                    || it.album.lowercase().contains(searchText.lowercase())

        }

        if (foundedMusics.isNotEmpty()) {
            stickyHeader {
                SearchType(
                    title = strings.musics,
                    primaryColor = primaryColor,
                    textColor = textColor
                )
            }
            items(foundedMusics) { music ->
                MusicItemComposable(
                    music = music,
                    onClick = { selectedMusic ->
                        coroutineScope.launch {
                            focusManager.clearFocus()
                            playerDraggableState.animateTo(
                                BottomSheetStates.EXPANDED,
                                tween(UiConstants.AnimationDuration.normal)
                            )
                        }.invokeOnCompletion { _ ->
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = selectedMusic,
                                musicList = foundedMusics as ArrayList<Music>,
                                playlistId = null,
                                isMainPlaylist = isMainPlaylist,
                                isForcingNewPlaylist = true
                            )
                        }
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onSelectedMusicForBottomSheet(music)
                        }
                    },
                    musicCover = retrieveCoverMethod(music.coverId),
                    textColor = textColor,
                    isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(music.musicId)
                )
            }
        }
        item {
            SoulPlayerSpacer()
        }
    }
}
