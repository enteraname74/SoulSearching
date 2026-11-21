package com.github.enteraname74.soulsearching.feature.settings.shortcuts

import org.jetbrains.compose.resources.stringArrayResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsShortcutsScreen(
    state: SettingsShortcutsState,
    actions: SettingsShortcutsActions,
) {
    SettingPage(
        title = CoreRes.string.shortcuts_title.toTextSpec(),
        navigateBack = actions::navigateBack,
    ) {
        stickyHeader {
            SoulText(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoulSearchingColorTheme.colorScheme.primary)
                    .padding(all = UiConstants.Spacing.large),
                text = CoreRes.string.shortcut_player_category.toTextSpec(),
                style = UiConstants.Typography.bodyTitle,
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
        }
        items(
            items = state.shortcuts
        ) {
            ShortcutView(it)
        }
    }
}

@Composable
private fun ShortcutView(shortcut: SettingsShortcutsState.Shortcut) {
    FlowRow(
        modifier = Modifier
            .padding(all = UiConstants.Spacing.large),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        val commands = stringArrayResource(shortcut.commands)
        commands.forEachIndexed { index, command ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SoulSearchingColorTheme.colorScheme.secondary,
                    contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                )
            ) {
                SoulText(
                    modifier = Modifier
                        .padding(UiConstants.Spacing.large),
                    text = command.toTextSpec(),
                    style = UiConstants.Typography.body,
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                )
            }

            if (index < commands.lastIndex) {
                SoulText(
                    modifier = Modifier
                        .padding(UiConstants.Spacing.large),
                    text = "+".toTextSpec(),
                    style = UiConstants.Typography.body,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                )
            }
        }
        SoulText(
            modifier = Modifier
                .padding(start = UiConstants.Spacing.large),
            text = shortcut.description,
            style = UiConstants.Typography.body.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
    }
}