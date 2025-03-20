package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.screen.SoulTemplateComposable
import com.github.enteraname74.soulsearching.coreui.screen.TemplateScreenButtonSpec
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeight

class GitHubReleaseBottomSheet(
    private val onClose: () -> Unit,
    private val onNavigateToGithubReleasePermission: () -> Unit,
) : SoulBottomSheet {

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose
        ) { closeWithAnim ->
            val windowHeightBeforeBarPadding: Float = rememberWindowHeight() - getStatusBarPadding()
            var bottomSheetHeight: Int by rememberSaveable {
                mutableIntStateOf(0)
            }

            val shouldUseStatusBar = bottomSheetHeight > windowHeightBeforeBarPadding

            val topPadding = if (shouldUseStatusBar) 0.dp else UiConstants.Spacing.medium

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = UiConstants.Spacing.medium,
                        top = topPadding,
                    )
                    .onGloballyPositioned { layoutCoordinates ->
                        bottomSheetHeight = layoutCoordinates.size.height
                    },
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            ) {
                SoulTopBar(
                    title = strings.activateGithubReleaseFetchTitle,
                    leftAction = object: TopBarActionSpec {
                        override val icon = Icons.Rounded.Cancel
                        override val onClick = closeWithAnim
                    },
                    withStatusBarPadding = shouldUseStatusBar,
                    colors = SoulTopBarDefaults.secondary(),
                )
                SoulTemplateComposable(
                    icon = Icons.Rounded.NewReleases,
                    iconSize = UiConstants.ImageSize.large,
                    text = strings.activateGithubReleaseFetchText,
                    buttonSpec = TemplateScreenButtonSpec(
                        text = strings.goToSettings,
                        onClick = {
                            closeWithAnim()
                            onNavigateToGithubReleasePermission()
                        },
                        colors = { SoulButtonDefaults.primaryColors() },
                    )
                )
            }
        }
    }
}