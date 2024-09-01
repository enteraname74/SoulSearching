package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

@Composable
fun MainMenuHeaderComposable(
    settingsAction: () -> Unit,
    searchAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(
                start = UiConstants.Spacing.medium,
                end = UiConstants.Spacing.medium,
                top = UiConstants.Spacing.small,
                bottom = UiConstants.Spacing.small
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val windowSize = rememberWindowSize()

        if (windowSize != WindowSize.Large) {
            SoulIconButton(
                icon = Icons.Rounded.Settings,
                contentDescription = strings.settingsAccessButton,
                colors = SoulButtonDefaults.colors(
                    contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    containerColor = Color.Transparent,
                ),
                onClick = settingsAction,
                size = ICON_SIZE,
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(ICON_SIZE)
            )
        }

        Text(
            text = strings.appName,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        SoulIconButton(
            icon = Icons.Rounded.Search,
            contentDescription = strings.searchAll,
            colors = SoulButtonDefaults.colors(
                contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                containerColor = Color.Transparent,
            ),
            onClick = searchAction,
            size = ICON_SIZE,
        )
    }
}

private val ICON_SIZE: Dp = 30.dp