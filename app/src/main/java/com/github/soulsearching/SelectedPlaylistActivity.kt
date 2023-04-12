package com.github.soulsearching

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.github.soulsearching.composables.screens.PlaylistScreen
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.MusicViewModel
import com.github.soulsearching.viewModels.SelectedPlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SelectedPlaylistActivity : ComponentActivity() {
    private val viewModel : SelectedPlaylistViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SoulSearchingTheme {
                val state by viewModel.state.collectAsState()

                var isPlaylistFetched by rememberSaveable {
                    mutableStateOf(false)
                }
                if (!isPlaylistFetched) {
                    viewModel.setSelectedPlaylist(UUID.fromString(intent.extras?.getString("playlistId")))
                    isPlaylistFetched = true
                }

                PlaylistScreen(
                    onPlaylistEvent = viewModel::onPlaylistsEvent,
                    onMusicEvent = viewModel::onMusicEvent,
                    selectedPlaylistState = state
                )
            }
        }
    }
}