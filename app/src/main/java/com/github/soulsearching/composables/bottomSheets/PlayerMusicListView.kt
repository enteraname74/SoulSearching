package com.github.soulsearching.composables.bottomSheets

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.R
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.composables.MusicList
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerMusicListView(
    maxHeight: Float,
    swipeableState: SwipeableState<BottomSheetStates>,
    coverList: ArrayList<ImageCover>,
    contentColor: Color,
    textColor: Color,
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    musicListSwipeableState: SwipeableState<BottomSheetStates>,
    playlistId: UUID?,
    playerMusicListViewModel: PlayerMusicListViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    BackHandler(swipeableState.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            swipeableState.animateTo(BottomSheetStates.MINIMISED, tween(300))
        }
    }

    val mainBoxClickableModifier =
        Modifier.clickable {
            coroutineScope.launch {
                when(swipeableState.currentValue) {
                    BottomSheetStates.MINIMISED -> swipeableState.animateTo(BottomSheetStates.EXPANDED, tween(300))
                    else -> swipeableState.animateTo(BottomSheetStates.MINIMISED, tween(300))
                }
            }
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = 0,
                    y = swipeableState.offset.value.roundToInt()
                )
            }
            .swipeable(
                state = swipeableState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    0f to BottomSheetStates.EXPANDED,
                    (maxHeight - 140f) to BottomSheetStates.MINIMISED
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = contentColor,
                    shape = RoundedCornerShape(
                        topStartPercent = 8,
                        topEndPercent = 8
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .composed {
                        mainBoxClickableModifier
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.played_list),
                    color = textColor,
                    fontSize = 15.sp
                )
            }
            MusicList(
                musicState = musicState,
                playlistState = playlistState,
                onMusicEvent = onMusicEvent,
                onPlaylistEvent = onPlaylistEvent,
                navigateToModifyMusic = navigateToModifyMusic,
                modifier = Modifier,
                retrieveCoverMethod = {uuid ->
                    coverList.find { it.coverId == uuid }?.cover
                },
                swipeableState = musicListSwipeableState,
                playlistId = playlistId,
                playerMusicListViewModel = playerMusicListViewModel
            )
        }
    }
}
