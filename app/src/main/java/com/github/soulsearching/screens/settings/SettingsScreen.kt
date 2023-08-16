package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.settings.SettingsElement
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsScreen(
    finishAction: () -> Unit,
    navigateToManageMusics: () -> Unit,
    navigateToColorTheme: () -> Unit,
    navigateToPersonalisation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.settings),
            leftAction = finishAction
        )
        LazyColumn {
            item {
                SettingsElement(
                    title = stringResource(id = R.string.manage_musics_title),
                    text = stringResource(id = R.string.manage_musics_text),
                    icon = Icons.Rounded.MusicNote,
                    clickAction = navigateToManageMusics
                )
                SettingsElement(
                    title = stringResource(id = R.string.color_theme_title),
                    text = stringResource(id = R.string.color_theme_text),
                    icon = Icons.Rounded.Palette,
                    clickAction = navigateToColorTheme
                )
                SettingsElement(
                    title = stringResource(id = R.string.personalisation_title),
                    text = stringResource(id = R.string.personalisation_text),
                    icon = Icons.Rounded.Edit,
                    clickAction = navigateToPersonalisation
                )
            }
        }
    }
}