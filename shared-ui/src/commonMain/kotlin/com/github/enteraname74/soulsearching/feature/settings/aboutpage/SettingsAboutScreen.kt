package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.composable.SettingsAboutComposable
import com.github.enteraname74.soulsearching.domain.AppVersion
import com.github.enteraname74.soulsearching.feature.settings.developers.SettingsDevelopersScreen
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Represent the view of the settings about screen.
 */
class SettingsAboutScreen : Screen, KoinComponent {
    private val feedbackPopUpManager: FeedbackPopUpManager by inject()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()

        SettingsAboutComposable(
            versionName = AppVersion.versionName,
            versionNameAction = {
                coroutineScope.launch {
                    feedbackPopUpManager.showFeedback(
                        feedback = strings.versionNameActionText,
                    )
                }
            },
            navigateToDevelopers = { navigator.push(SettingsDevelopersScreen()) },
            finishAction = { navigator.pop() }
        )
    }
}
