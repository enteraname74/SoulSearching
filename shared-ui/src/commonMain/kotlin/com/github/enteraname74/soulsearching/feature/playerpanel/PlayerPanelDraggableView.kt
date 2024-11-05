package com.github.enteraname74.soulsearching.feature.playerpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.PlayerPanelContent
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Suppress("Deprecation")
fun PlayerPanelDraggableView(
    maxHeight: Float,
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
    playerState: PlayerViewState.Data,
    onSelectedMusic: (Music) -> Unit,
    onRetrieveLyrics: () -> Unit,
    secondaryColor: Color,
    textColor: Color,
    subTextColor: Color,
    buttonColors: SoulButtonColors,
) {
    val coroutineScope = rememberCoroutineScope()

    val isExpanded by remember {
        derivedStateOf {
            playerMusicListViewManager.currentValue == BottomSheetStates.EXPANDED
        }
    }

    SoulBackHandler(isExpanded) {
        coroutineScope.launch {
            playerMusicListViewManager.animateTo(BottomSheetStates.COLLAPSED)
        }
    }

    val collapsedSize: Float = PlayerUiUtils.getDraggablePanelCollapsedOffset()
    val statusParPadding: Int = getStatusBarPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = 0,
                    y = max(playerMusicListViewManager.offset.roundToInt(), 0)
                )
            }
            .swipeable(
                state = playerMusicListViewManager.musicListDraggableState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    statusParPadding.toFloat() to BottomSheetStates.EXPANDED,
                    (maxHeight - collapsedSize) to BottomSheetStates.COLLAPSED,
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = secondaryColor,
                    shape = RoundedCornerShape(
                        topStartPercent = 4,
                        topEndPercent = 4
                    )
                )
                .navigationBarsPadding()
        ) {
            DragHandler(subTextColor = subTextColor)
            PlayerPanelContent(
                playerState = playerState,
                onSelectedMusic = onSelectedMusic,
                onRetrieveLyrics = onRetrieveLyrics,
                textColor = textColor,
                subTextColor = subTextColor,
                isExpanded = isExpanded,
                buttonColors = buttonColors,
            )
        }
    }
}

@Composable
private fun DragHandler(
    subTextColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(
            modifier = Modifier
                .background(
                    color = subTextColor,
                    shape = RoundedCornerShape(percent = 50)
                )
                .height(4.dp)
                .width(40.dp)
        )
    }
}
