package com.github.soulsearching.settings.developers.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.settings.presentation.composable.SettingsElement
import com.github.soulsearching.settings.developers.domain.model.Developer
import com.github.soulsearching.strings.strings
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme

/**
 * Represent the view of the developers screen.
 */
class SettingsDevelopersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsDevelopersScreenView(
            finishAction = {
                navigator.pop()
            }
        )
    }
}

@Composable
fun SettingsDevelopersScreenView(
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
                    text = developer.function,
                    isClickable = false
                )
            }
            item {
                PlayerSpacer()
            }
        }
    }
}
