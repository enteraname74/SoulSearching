package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.classes.Developer
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.setting.SettingsElement
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsDevelopersScreen(
    finishAction: () -> Unit
) {
    val developers = listOf(
        Developer(
            name = "Noah Penin",
            function = stringResource(id = R.string.lead_developer)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.developers_title),
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
