package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.AppVersion
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.settings.developers.SettingsDevelopersScreen
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsElement
import kotlinx.coroutines.launch

/**
 * Represent the view of the settings about screen.
 */
class SettingsAboutScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsAboutComposable(
            navigateToDevelopers = { navigator.safePush(SettingsDevelopersScreen()) },
            navigateBack = { navigator.pop() }
        )
    }
}

@Composable
private fun SettingsAboutComposable(
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
            SettingsElement(
                title = strings.developersTitle,
                subTitle = strings.developersText,
                onClick = navigateToDevelopers
            )
        }
        item {
            SettingsElement(
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
