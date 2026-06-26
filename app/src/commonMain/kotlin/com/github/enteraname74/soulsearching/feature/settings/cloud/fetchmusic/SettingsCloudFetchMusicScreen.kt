package com.github.enteraname74.soulsearching.feature.settings.cloud.fetchmusic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudFetchMusicScreen(
    actions: SettingsCloudFetchMusicActions,
    state: SettingsCloudFetchMusicState,
) {
    val focusManager = LocalFocusManager.current

    SettingPage(
        title = strings.cloudFetchMusicTitle,
        navigateBack = actions::navigateBack,
        contentPadding = PaddingValues(all = UiConstants.Spacing.large),
        verticalPadding = UiConstants.Spacing.medium,
    ) {
        item {
            state.urlField.TextField(
                modifier = Modifier.fillMaxWidth(),
                focusManager = LocalFocusManager.current,
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = UiConstants.Spacing.mediumPlus,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                SoulFilledButton(
                    enabled = state.urlField.value.isNotBlank(),
                    text = strings.cloudFetchMusicButton,
                    onClick = {
                        focusManager.clearFocus()
                        actions.fetch()
                    },
                )
            }
        }
    }
}