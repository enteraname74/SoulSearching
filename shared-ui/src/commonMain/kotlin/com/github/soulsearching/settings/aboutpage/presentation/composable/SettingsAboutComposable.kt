package com.github.soulsearching.settings.aboutpage.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.soulsearching.composables.SouTopBar
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.soulsearching.settings.presentation.composable.SettingsElement

@Composable
fun SettingsAboutComposable(
    versionName: String,
    versionNameAction: () -> Unit,
    navigateToDevelopers: () -> Unit,
    finishAction: () -> Unit
) {
    var clickCount by rememberSaveable {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        SouTopBar(
            title = strings.aboutTitle,
            leftAction = finishAction
        )
        LazyColumn {
            item {
                SettingsElement(
                    title = strings.developersTitle,
                    text = strings.developersText,
                    clickAction = navigateToDevelopers
                )
            }
            item {
                SettingsElement(
                    title = strings.versionNameTitle,
                    text = versionName,
                    clickAction = {
                        clickCount += 1
                        if (clickCount == 10) {
                            versionNameAction()
                            clickCount = 0
                        }
                    }
                )
            }
            item {
                SoulPlayerSpacer()
            }
        }
    }
}