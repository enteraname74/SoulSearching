package com.github.enteraname74.soulsearching.feature.settings.colortheme.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun PersonalizedColorThemeCard(
    onClick: () -> Unit,
    isSelected: Boolean,
    hasPlayerTheme: Boolean,
    hasPlaylistTheme: Boolean,
    hasOtherViewsTheme: Boolean,
    togglePersonalizedDynamicPlayerTheme: () -> Unit,
    togglePersonalizedDynamicPlaylistTheme: () -> Unit,
    togglePersonalizedDynamicOtherViewsTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ColorCard(
        modifier = modifier,
        title = CoreRes.string.personalized_theme_title.toTextSpec(),
        text = CoreRes.string.personalized_theme_text.toTextSpec(),
        onClick = onClick,
        isSelected = isSelected,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
        ) {
            SoulMenuSwitch(
                title = CoreRes.string.dynamic_player_view.toTextSpec(),
                toggleAction = { if (isSelected) togglePersonalizedDynamicPlayerTheme() },
                isChecked = hasPlayerTheme,
                titleColor = SoulSearchingColorTheme.colorScheme.text,
                textColor = SoulSearchingColorTheme.colorScheme.textSecondary,
                padding = PaddingValues(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.small,
                ),
            )
            SoulMenuSwitch(
                title = CoreRes.string.dynamic_playlist_view.toTextSpec(),
                toggleAction = { if (isSelected) togglePersonalizedDynamicPlaylistTheme() },
                isChecked = hasPlaylistTheme,
                titleColor = SoulSearchingColorTheme.colorScheme.text,
                textColor = SoulSearchingColorTheme.colorScheme.textSecondary,
                padding = PaddingValues(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.small,
                ),
            )
            SoulMenuSwitch(
                title = CoreRes.string.dynamic_other_view.toTextSpec(),
                toggleAction = { if (isSelected) togglePersonalizedDynamicOtherViewsTheme() },
                isChecked = hasOtherViewsTheme,
                titleColor = SoulSearchingColorTheme.colorScheme.text,
                textColor = SoulSearchingColorTheme.colorScheme.textSecondary,
                padding = PaddingValues(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.small,
                ),
            )
        }
    }
}
