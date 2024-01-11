package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.SettingsElement
import com.github.soulsearching.model.Developer
import com.github.soulsearching.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme

@Composable
fun SettingsDevelopersScreen(
    finishAction: () -> Unit
) {
    val developers = listOf(
        Developer(
            name = "Noah Penin",
            function = strings.leadDeveloper
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        AppHeaderBar(
            title = strings.developersTitle,
            leftAction = finishAction
        )
        LazyColumn {
            items(developers) {developer ->
                SettingsElement(
                    title = developer.name,
                    text = developer.function
                )
            }
            item {
                PlayerSpacer()
            }
        }
    }
}
