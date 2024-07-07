package com.github.enteraname74.soulsearching.feature.settings.developers.presentation

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
import com.github.soulsearching.composables.SouTopBar
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsElement
import com.github.enteraname74.soulsearching.feature.settings.developers.domain.model.Developer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

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
        SouTopBar(
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
                SoulPlayerSpacer()
            }
        }
    }
}
