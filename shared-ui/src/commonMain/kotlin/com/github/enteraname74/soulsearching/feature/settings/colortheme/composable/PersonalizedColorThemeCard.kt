package com.github.enteraname74.soulsearching.feature.settings.colortheme.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsSwitchElement

@Composable
fun PersonalizedColorThemeCard(
    onClick: () -> Unit,
    isSelected: Boolean,
    padding: Dp = UiConstants.Spacing.large,
    colorThemeManager: ColorThemeManager = injectElement()
) {
    ColorCard(
        title = strings.personalizedThemeTitle,
        text = strings.personalizedThemeText,
        onClick = onClick,
        isSelected = isSelected,
        padding = padding
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = UiConstants.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
        ) {
            SettingsSwitchElement(
                title = strings.dynamicPlayerView,
                toggleAction = { if (isSelected) colorThemeManager.toggleDynamicPlayerTheme() },
                isChecked = colorThemeManager.isDynamicPlayerThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = strings.dynamicPlaylistView,
                toggleAction = { if (isSelected) colorThemeManager.toggleDynamicPlaylistTheme() },
                isChecked = colorThemeManager.isDynamicPlaylistThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = strings.dynamicOtherView,
                toggleAction = { if (isSelected) colorThemeManager.toggleDynamicOtherViewsTheme() },
                isChecked = colorThemeManager.isDynamicOtherViewsThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
        }
    }
}