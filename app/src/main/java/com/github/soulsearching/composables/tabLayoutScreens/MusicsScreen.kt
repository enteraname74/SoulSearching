package com.github.soulsearching.composables.tabLayoutScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.MusicList
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState

@Composable
fun MusicsScreen(
    state: MusicState,
    onEvent: (MusicEvent) -> Unit
) {
    MusicList(
        state = state,
        onEvent = onEvent,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(start = Constants.Spacing.medium, end = Constants.Spacing.medium)
    )
}