package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanelUiUtils
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.ext.toPx
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeight
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeightDp
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidthDp
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager

object PlayerUiUtils {
    private val minHeightForRowView: Dp = 500.dp

    @Composable
    fun canShowRowControlPanel(): Boolean {
        val windowSize = rememberWindowSize()
        val maxHeightDp = rememberWindowHeightDp()
        return windowSize != WindowSize.Small && maxHeightDp <= minHeightForRowView
    }

    @Composable
    fun canShowSidePanel(): Boolean {
        val maxHeightDp = rememberWindowHeightDp()
        val maxWidth = rememberWindowWidthDp()
        val playerViewWidth = if (NavigationPanelUiUtils.canShowPanel()) {
            maxWidth - NavigationPanelUiUtils.PanelWidth
        } else {
            maxWidth
        }
        val freeSpaceForPanel: Dp = playerViewWidth - getImageSize()
        val hasWidth = freeSpaceForPanel >= MinSidePanelWidth
        val hasHeight = maxHeightDp > minHeightForRowView

        return hasHeight && hasWidth
    }

    @Composable
    fun getDraggablePanelCollapsedOffset(): Float {
        val maxHeightDp = rememberWindowHeightDp()
        return if (maxHeightDp <= minHeightForRowView) {
            0f
        } else {
            val baseHeight = 64.dp.toPx()
            baseHeight + getNavigationBarPadding()
        }
    }

    @Composable
    private fun getTransitionRatio(
        playerViewManager: PlayerViewManager = injectElement(),
    ): Float {
        val maxHeight: Float = rememberWindowHeight()
        val currentOffset = playerViewManager.offset

        val maxOffset: Float = maxHeight - PlayerMinimisedHeight
        return currentOffset / maxOffset
    }

    @Composable
    fun getAlphaTransition(): Float {
        val ratio = getTransitionRatio()
        val alpha = 1f - ratio
        val fixedAlpha = alpha.coerceIn(
            minimumValue = 0f,
            maximumValue = 1f,
        )
        return fixedAlpha
    }

    @Composable
    fun getImageSize(): Dp {
        val maxHeight: Dp = rememberWindowHeightDp()
        val ratio = getTransitionRatio()

        val maxImageSizeRatio = if (rememberWindowSize() != WindowSize.Small) {
            2f
        } else {
            2.45f
        }

        val maxImageSize = max(
            maxHeight / maxImageSizeRatio,
            MinImageSize,
        )

        val size = max(
            maxImageSize * (1f - ratio),
            MinImageSize,
        )
        val coerced = size.coerceIn(
            minimumValue = MinImageSize,
            maximumValue = maxImageSize,
        )

        return coerced
    }

    @Composable
    fun getImageHorizontalPadding(
        imageSize: Dp,
        maxWidth: Dp,
    ): Dp {
        val ratio = getTransitionRatio()
        val alpha = 1f - ratio

        val padding = max(
            ((maxWidth - imageSize) / 2) * alpha,
            MinImagePaddingStart,
        )

        return padding
    }

    @Composable
    fun getPlayerControlsWidth(
        imageSize: Dp,
    ): Dp {
        val maxWidth: Dp = rememberWindowWidthDp()
        return min(
            imageSize + PlayerControlsExtraWidth,
            maxWidth,
        )
    }

    @Composable
    fun getImageTopPadding(
        expandedMainInformationHeight: Int,
        imageSize: Dp
    ): Dp =
        if (canShowRowControlPanel()) {
            getImageTopPaddingForRowView(imageSize)
        } else {
            getImageTopPaddingForColumnView(expandedMainInformationHeight)
        }

    @Composable
    fun getTopInformationBottomPadding(): Dp {
        val maxHeight: Dp = rememberWindowHeightDp()
        return maxHeight / 20
    }

    @Composable
    fun getImageTopPaddingForColumnView(
        expandedMainInformationHeight: Int
    ): Dp {

        val mainInfoHeightDp: Dp = expandedMainInformationHeight.toDp() + getTopInformationBottomPadding()

        val ratio = getTransitionRatio()
        val alpha = 1f - ratio

        return max(
            mainInfoHeightDp * alpha,
            MinImagePaddingTop
        )
    }

    @Composable
    fun getImageTopPaddingForRowView(
        imageSize: Dp
    ): Dp {

        val maxHeight = rememberWindowHeightDp()
        val topPadding = (maxHeight - imageSize) / 2

        val ratio = getTransitionRatio()
        val alpha = 1f - ratio

        return max(
            topPadding * alpha,
            MinImagePaddingTop,
        )
    }

    val MinImageSize: Dp
        @Composable
        get() = UiConstants.CoverSize.small

    val MinImagePaddingStart: Dp = 4.dp
    val MinImagePaddingTop: Dp = 4.dp

    val PlayerControlsExtraWidth: Dp = 25.dp

    val MinPlayerSidePanelWidth: Dp = 50.dp
    val MaxPlayerSidePanelWidth: Dp = 600.dp

    val MinSidePanelWidth: Dp = 400.dp

    const val PLAYER_BACKGROUND_COLOR_LABEL: String = "PLAYER_BACKGROUND_COLOR_LABEL"
}
