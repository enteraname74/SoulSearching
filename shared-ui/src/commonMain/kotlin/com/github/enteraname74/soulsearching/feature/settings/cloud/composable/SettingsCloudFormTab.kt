package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.animation.VerticalAnimatedVisibility
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SettingsCloudFormTab(
    textFields: List<SoulTextFieldHolder>,
    error: String?,
    focusManager: FocusManager,
    validateButtonTitle: String,
    onValidate: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                all = UiConstants.Spacing.mediumPlus
            ),
    ) {
        textFields.forEach {
            it.TextField(
                modifier = Modifier,
                focusManager = focusManager,
            )
        }
        VerticalAnimatedVisibility(
            visible = error != null,
        ) {
            error?.let { error ->
                Text(
                    text = error,
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                    style = UiConstants.Typography.body,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = UiConstants.Spacing.medium),
            contentAlignment = Alignment.CenterEnd,
        ) {
            SoulButton(
                onClick = onValidate,
                colors = SoulButtonDefaults.primaryColors(),
                text = validateButtonTitle,
            )
        }
    }
}