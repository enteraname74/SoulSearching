package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanelUiUtils
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.toPx
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeight
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeightDp
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidthDp
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerUiView
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager

object PlayerUiUtils {
    private val maxHeightForRowView: Dp = 500.dp

    @Composable
    fun getPlayerUiView(): PlayerUiView =
        when {
            showRowControlPanel() -> PlayerUiView.SmallRow
            canShowSidePanel() -> PlayerUiView.LargeScreen
            else -> PlayerUiView.DefaultMobile
        }

    @Composable
    fun showRowControlPanel(): Boolean {
        val windowSize = rememberWindowSize()
        val maxHeightDp = rememberWindowHeightDp()
        return windowSize != WindowSize.Small && maxHeightDp <= maxHeightForRowView
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
        val hasWidth = playerViewWidth > 800.dp
        val hasHeight = maxHeightDp > maxHeightForRowView

        return hasHeight && hasWidth
    }

    @Composable
    fun canShowMoreInfoOnMinimisedView(): Boolean {
        val maxWidth = rememberWindowWidthDp()
        val playerViewWidth = if (NavigationPanelUiUtils.canShowPanel()) {
            maxWidth - NavigationPanelUiUtils.PanelWidth
        } else {
            maxWidth
        }

        return playerViewWidth > 700.dp
    }

    @Composable
    fun getDraggablePanelCollapsedOffset(): Float {
        val maxHeightDp = rememberWindowHeightDp()
        return if (maxHeightDp <= maxHeightForRowView) {
            0f
        } else {
            val baseHeight = 64.dp.toPx()
            baseHeight + getNavigationBarPadding()
        }
    }

    @Composable
    fun getCoverUiInformation(
        maxHeight: Dp,
        maxWidth: Dp,
        availableHeight: Dp,
        topInfoHeight: Dp,
        playerUiView: PlayerUiView,
    ): CoverUiInformation =
        when (playerUiView) {
            PlayerUiView.LargeScreen -> {
                // In large row view, the size of the cover is a portion of the available space.
                val imageSize = simpleImageSize(
                    maxHeight = maxHeight,
                    maxWidth = maxWidth * .7f,
                )
                // Image is horizontally centered
                val horizontalPadding = (maxWidth - imageSize) / 2

                // Image is vertically centered
                val topPadding = topInfoHeight + UiConstants.Spacing.medium + (maxHeight - imageSize) / 2
                CoverUiInformation(
                    size = imageSize,
                    topPadding = max(
                        topPadding * getTransitionRatio(),
                        MinImagePaddingTop,
                    ),
                    horizontalPadding = max(
                        horizontalPadding * getTransitionRatio(),
                        MinImagePaddingStart,
                    )
                )
            }
            PlayerUiView.DefaultMobile -> {
                // In default mobile view, the size of the cover is a portion of the available space.
                val imageSize = simpleImageSize(
                    maxHeight = maxHeight,
                    maxWidth = maxWidth * .85f,
                )
                // Image is horizontally centered
                val horizontalPadding = (maxWidth - imageSize) / 2

                // Image is vertically centered
                val topPadding = topInfoHeight + UiConstants.Spacing.large + (maxHeight - imageSize) / 2
                CoverUiInformation(
                    size = imageSize,
                    topPadding = max(
                        topPadding * getTransitionRatio(),
                        MinImagePaddingTop,
                    ),
                    horizontalPadding = max(
                        horizontalPadding * getTransitionRatio(),
                        MinImagePaddingStart,
                    )
                )
            }
            PlayerUiView.SmallRow -> {
                // In small row view, the size of the cover is the biggest we can have.
                val imageSize = simpleImageSize(
                    maxHeight = maxHeight,
                    maxWidth = maxWidth,
                )
                // Image is vertically centered
                val topPadding = topInfoHeight + (availableHeight - imageSize) / 2

                // Image is horizontally centered
                val horizontalPadding = (maxWidth - imageSize) / 2

                CoverUiInformation(
                    size = imageSize,
                    topPadding = max(
                        topPadding * getTransitionRatio(),
                        MinImagePaddingTop,
                    ),
                    horizontalPadding = max(
                        horizontalPadding * getTransitionRatio(),
                        MinImagePaddingStart,
                    )
                )
            }
        }

    @Composable
    fun simpleImageSize(
        maxHeight: Dp,
        maxWidth: Dp,
    ): Dp {
        val ratio = getTransitionRatio()
        val maxSize = min(maxHeight, maxWidth)
        return max(maxSize * ratio, MinImageSize)
    }

    @Composable
    fun getTransitionRatio(): Float {
        val playerViewManager: PlayerViewManager = injectElement()
        val currentOffset = playerViewManager.offset
        val maxOffset: Float = rememberWindowHeight() - PlayerMinimisedHeight
        return 1 - (currentOffset / maxOffset).coerceIn(0f, 1f)
    }

    val MinImageSize: Dp
        @Composable
        get() = UiConstants.CoverSize.small

    val MinImagePaddingStart: Dp = 4.dp
    val MinImagePaddingTop: Dp = 4.dp
    const val PLAYER_BACKGROUND_COLOR_LABEL: String = "PLAYER_BACKGROUND_COLOR_LABEL"
}

data class CoverUiInformation(
    val size: Dp,
    val topPadding: Dp,
    val horizontalPadding: Dp,
)