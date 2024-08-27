package com.github.enteraname74.soulsearching.feature.settings.personalisation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsSwitchElement

/**
 * Represent the view of the player view personalisation screen in the settings.
 */
class SettingsPlayerViewPersonalisationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsPlayerViewPersonalisationScreenView(
            onBack = {
                navigator.pop()
            }
        )
    }

    @Composable
    private fun SettingsPlayerViewPersonalisationScreenView(
        onBack: () -> Unit,
        viewSettingsManager: ViewSettingsManager = injectElement()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoulSearchingColorTheme.colorScheme.primary)
        ) {
            SoulTopBar(
                title = strings.managePlayerTitle,
                leftAction = TopBarNavigationAction(
                    onClick = onBack,
                )
            )
            LazyColumn {
                item {
                    SettingsSwitchElement(
                        title = strings.playerSwipeTitle,
                        toggleAction = { viewSettingsManager.togglePlayerSwipe() },
                        isChecked = viewSettingsManager.isPlayerSwipeEnabled,
                        titleFontSize = 16.sp,
                        padding = PaddingValues(
                            start = UiConstants.Spacing.veryLarge,
                            end = UiConstants.Spacing.veryLarge,
                            top = UiConstants.Spacing.veryLarge,
                            bottom = UiConstants.Spacing.medium
                        ),
                        maxLines = 3
                    )
                }
                item {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}