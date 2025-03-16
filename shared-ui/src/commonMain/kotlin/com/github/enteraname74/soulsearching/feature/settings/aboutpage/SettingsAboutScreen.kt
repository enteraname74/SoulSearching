package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.animation.VerticalAnimatedVisibility
import com.github.enteraname74.soulsearching.coreui.card.SoulInformationCard
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.AppVersion
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.developers.SettingsDevelopersScreen
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.domain.SettingsAboutState
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.domain.SettingsAboutViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import kotlinx.coroutines.launch

/**
 * Represent the view of the settings about screen.
 */
class SettingsAboutScreen : Screen, SettingPage {
    private val CardMinWidth: Dp = 0.dp
    private val CardMaxWidth: Dp = 500.dp

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsAboutViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state by screenModel.state.collectAsState()

        Screen(
            navigateToDevelopers = { navigator.safePush(SettingsDevelopersScreen()) },
            navigateBack = { navigator.pop() },
            state = state,
        )
    }

    @Composable
    private fun LatestReleaseCard(
        release: Release,
    ) {
        val uriHandler = LocalUriHandler.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UiConstants.Spacing.large),
            contentAlignment = Alignment.Center,
        ) {
            SoulInformationCard(
                modifier = Modifier
                    .widthIn(
                        min = CardMinWidth,
                        max = CardMaxWidth,
                    ).clickableWithHandCursor {
                        uriHandler.openUri(release.githubUrl)
                    },
                title = strings.newReleaseAvailableTitle,
                text = strings.newReleaseAvailableText(
                    releaseName = release.name,
                ),
                icon = Icons.Rounded.NewReleases,
                buttonSpec = null,
            )
        }
    }

    @Composable
    private fun Screen(
        state: SettingsAboutState,
        navigateToDevelopers: () -> Unit,
        navigateBack: () -> Unit,
        feedbackPopUpManager: FeedbackPopUpManager = injectElement()
    ) {
        var clickCount by rememberSaveable {
            mutableIntStateOf(0)
        }

        val coroutineScope = rememberCoroutineScope()

        SettingPage(
            navigateBack = navigateBack,
            title = strings.aboutTitle,
        ) {
            item {
                VerticalAnimatedVisibility(
                    visible = state.moreRecentRelease != null,
                ) {
                    state.moreRecentRelease?.let {
                        LatestReleaseCard(it)
                    }
                }
            }
            item {
                SoulMenuElement(
                    title = strings.developersTitle,
                    subTitle = strings.developersText,
                    onClick = navigateToDevelopers
                )
            }
            item {
                SoulMenuElement(
                    title = strings.versionNameTitle,
                    subTitle = AppVersion.versionName,
                    onClick = {
                        clickCount += 1
                        if (clickCount == 10) {
                            coroutineScope.launch {
                                feedbackPopUpManager.showFeedback(
                                    feedback = strings.versionNameActionText,
                                )
                            }
                            clickCount = 0
                        }
                    }
                )
            }
        }
    }
}


