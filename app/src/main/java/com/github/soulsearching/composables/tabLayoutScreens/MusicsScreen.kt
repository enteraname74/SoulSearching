package com.github.soulsearching.composables.tabLayoutScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState

@Composable
fun MusicsScreen(
    state: MusicState,
    onEvent: (MusicEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(Constants.Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
    ) {
        items(state.musics) { music ->
            MusicItemComposable(music = music, onClick = onEvent)
        }
    }
}