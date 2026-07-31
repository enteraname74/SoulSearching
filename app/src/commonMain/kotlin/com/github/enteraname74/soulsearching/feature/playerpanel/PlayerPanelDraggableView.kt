package com.github.enteraname74.soulsearching.feature.playerpanel

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanelUiUtils
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.composable.SoulSelectedIconDefaults
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.SwipeableViewManagerHandler
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.PlayerPanelContent
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Suppress("Deprecation")
fun PlayerPanelDraggableView(
    maxHeight: Float,
    playerState: PlayerViewState.Data,
    lyricsState: LyricsFetchState,
    onMoreClickedOnMusic: (musicId: Uuid) -> Unit,
    onLongSelectOnMusic: (Music) -> Unit,
    multiSelectionState: MultiSelectionState,
    closeSelection: () -> Unit,
    onActivateRemoteLyrics: () -> Unit,
    onSwiped: ((Music) -> Unit)?,
    onClickOnMusic: ((Music) -> Unit)?,
    onAddFromUrl: (() -> Unit)?,
    containerColor: Color,
    textColor: Color,
    subTextColor: Color,
    buttonColors: SoulButtonColors,
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
    multiSelectionManager: MultiSelectionManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    val isExpanded by remember {
        derivedStateOf {
            playerMusicListViewManager.currentValue == BottomSheetStates.EXPANDED
        }
    }

    SoulBackHandler(isExpanded) {
        coroutineScope.launch {
            if (multiSelectionManager.isActive()) {
                closeSelection()
            } else {
                playerMusicListViewManager.animateTo(BottomSheetStates.COLLAPSED)
            }
        }
    }

    SwipeableViewManagerHandler(
        swipeableViewManager = playerMusicListViewManager,
    )

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
                state = playerMusicListViewManager.draggableState,
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
                    color = containerColor,
                    shape = panelShape()
                )
                .navigationBarsPadding()
        ) {
            DragHandler(subTextColor = subTextColor)
            PlayerPanelContent(
                playerState = playerState,
                lyricsState = lyricsState,
                onMoreClickedOnMusic = onMoreClickedOnMusic,
                contentColor = textColor,
                containerColor = containerColor,
                subTextColor = subTextColor,
                isExpanded = isExpanded,
                buttonColors = buttonColors,
                onLongSelectOnMusic = onLongSelectOnMusic,
                multiSelectionState = multiSelectionState,
                selectedIconColors = SoulSelectedIconDefaults.primary(),
                onActivateRemoteLyrics = onActivateRemoteLyrics,
                onSwiped = onSwiped,
                onClickOnMusic = onClickOnMusic,
                onAddFromUrl = onAddFromUrl,
            )
        }
    }
}

@Composable
private fun panelShape(): Shape =
    if (NavigationPanelUiUtils.canShowPanel()) {
        RectangleShape
    } else {
        RoundedCornerShape(
            topStartPercent = 4,
            topEndPercent = 4
        )
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
