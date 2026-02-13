package com.github.enteraname74.soulsearching.feature.settings.colortheme.colorseed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorPaletteSeed
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsColorSeedScreen(
    navigateBack: () -> Unit,
    viewModel: SettingsColorSeedViewModel = koinViewModel(),
) {
    val currentSeed: ColorPaletteSeed by viewModel.currentSeed.collectAsStateWithLifecycle()

    SettingPage(
        navigateBack = navigateBack,
        title = strings.extractedColorTitle,
    ) {
        item { InformationCard() }
        items(
            items = ColorPaletteSeed.entries,
        ) { seed ->
            SeedRow(
                colorPaletteSeed = seed,
                isSelected = seed == currentSeed,
                onClick = { viewModel.setSeed(seed) }
            )
        }
    }
}

@Composable
private fun InformationCard() {
    Card(
        modifier = Modifier
            .padding(
                all = UiConstants.Spacing.large,
            )
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            containerColor = SoulSearchingColorTheme.colorScheme.secondary
        )
    ) {
        Text(
            modifier = Modifier
                .padding(
                    all = UiConstants.Spacing.large,
                ),
            text = strings.extractedColorInfo,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = UiConstants.Typography.body,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SeedRow(
    colorPaletteSeed: ColorPaletteSeed,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                all = UiConstants.Spacing.large,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            UiConstants.Spacing.medium,
        )
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                unselectedColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
        )
        Text(
            text = strings.colorPaletteSeed(seed = colorPaletteSeed),
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.body,
        )
    }
}