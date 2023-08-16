package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsPersonalisationScreen(
    finishAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.personalisation_title),
            leftAction = finishAction
        )
        LazyColumn {
        }
    }
}