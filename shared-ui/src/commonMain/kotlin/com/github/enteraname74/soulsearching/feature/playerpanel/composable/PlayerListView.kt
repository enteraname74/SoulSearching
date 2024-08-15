package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import kotlinx.coroutines.launch

@Composable
fun PlayerListView(
    playbackManager: PlaybackManager = injectElement(),
    isExpanded: Boolean,
    playedList: List<Music>,
    onSelectedMusic: (Music) -> Unit,
    coverList: ArrayList<ImageCover>,
    secondaryColor: Color,
    primaryColor: Color
) {
    
    val coroutineScope = rememberCoroutineScope()
    val playerListState = rememberLazyListState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UiConstants.Spacing.small)
        ) {
            Button(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = primaryColor
                ),
                onClick = {
                    coroutineScope.launch {
                        if (playbackManager.currentMusicIndex != -1) {
                            playerListState.animateScrollToItem(
                                playbackManager.currentMusicIndex
                            )
                        }
                    }
                }
            ) {
                Text(
                    text = strings.currentSong,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = secondaryColor,
                    fontSize = 12.sp
                )
            }
        }

        if (isExpanded) {
            LazyColumn(
                state = playerListState
            ) {
                items(
                    items = playedList,
                ) { elt ->
                    MusicItemComposable(
                        music = elt,
                        onClick = { music ->
                            playbackManager.setAndPlayMusic(music)
                        },
                        onLongClick = {
                            coroutineScope.launch {
                                onSelectedMusic(elt)
                            }
                        },
                        musicCover = coverList.find { it.coverId == elt.coverId }?.cover,
                        textColor = secondaryColor,
                        isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(elt.musicId)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = secondaryColor
                )
            }
        }
    }
}