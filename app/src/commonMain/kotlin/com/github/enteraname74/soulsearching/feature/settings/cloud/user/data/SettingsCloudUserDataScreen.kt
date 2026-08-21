package com.github.enteraname74.soulsearching.feature.settings.cloud.user.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.cloud.user.data.composable.SettingsCloudUserDataUserStorageView
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
internal fun SettingsCloudUserDataScreen(
    state: SettingsCloudUserDataState,
) {
    state.dialogState?.Dialog()

    SettingPage(
        navigateBack = state.onNavigateBack,
        title = strings.userStorageTitle,
        verticalPadding = UiConstants.Spacing.large,
        contentPadding = PaddingValues(all = UiConstants.Spacing.large)
    ) {
        state.userStorage?.let { userStorage ->
            item(
                key = UserStorageKey,
                contentType = UserStorageContentType,
            ) {
                SettingsCloudUserDataUserStorageView(
                    userStorage = userStorage,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        item(
            key = ClearButtonKey,
            contentType = ClearButtonContentType,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                SoulFilledButton(
                    text = strings.clearUserStorageButton,
                    onClick = state.onClearData,
                )
            }
        }
    }
}

private const val UserStorageKey: String = "UserStorageKey"
private const val ClearButtonKey: String = "ClearButtonKey"

private const val UserStorageContentType: String = "UserStorageContentType"
private const val ClearButtonContentType: String = "ClearButtonContentType"
