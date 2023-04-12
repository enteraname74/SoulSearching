package com.github.soulsearching

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.soulsearching.composables.MainMenuHeaderComposable
import com.github.soulsearching.composables.TabLayoutComposable
import com.github.soulsearching.composables.screens.TestButtons
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.MusicViewModel
import com.github.soulsearching.viewModels.AllPlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val musicViewModel: MusicViewModel by viewModels()
    private val allPlaylistsViewModel: AllPlaylistsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val musicState by musicViewModel.state.collectAsState()
            val playlistState by allPlaylistsViewModel.state.collectAsState()

            SoulSearchingTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    MainMenuHeaderComposable()

                    TestButtons(
                        onMusicEvent = musicViewModel::onMusicEvent,
                        onPlaylistEvent = allPlaylistsViewModel::onPlaylistsEvent
                    )

                    TabLayoutComposable(
                        musicState = musicState,
                        playlistsState = playlistState,
                        onMusicEvent = musicViewModel::onMusicEvent,
                        onPlaylistEvent = allPlaylistsViewModel::onPlaylistsEvent
                    )
                }
            }
        }
    }
}
