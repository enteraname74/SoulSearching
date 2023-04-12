package com.github.soulsearching

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.soulsearching.composables.screens.PlaylistScreen
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectedPlaylistActivity : ComponentActivity() {
    private val viewModel : MusicViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SoulSearchingTheme {
                val state by viewModel.state.collectAsState()

                PlaylistScreen(
                    state = state,
                    onEvent = viewModel::onMusicEvent
                )
            }
        }
    }
}