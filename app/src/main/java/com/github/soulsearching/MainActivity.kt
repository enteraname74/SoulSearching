package com.github.soulsearching

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
import com.github.soulsearching.viewModels.AllAlbumsViewModel
import com.github.soulsearching.viewModels.AllArtistsViewModel
import com.github.soulsearching.viewModels.AllMusicsViewModel
import com.github.soulsearching.viewModels.AllPlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val allMusicsViewModel: AllMusicsViewModel by viewModels()
    private val allPlaylistsViewModel: AllPlaylistsViewModel by viewModels()
    private val allAlbumsViewModel: AllAlbumsViewModel by viewModels()
    private val allArtistsViewModel: AllArtistsViewModel by viewModels()

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val musicState by allMusicsViewModel.state.collectAsState()
            val playlistState by allPlaylistsViewModel.state.collectAsState()
            val albumState by allAlbumsViewModel.state.collectAsState()
            val artistState by allArtistsViewModel.state.collectAsState()

            SoulSearchingTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    MainMenuHeaderComposable()

                    TestButtons(
                        onMusicEvent = allMusicsViewModel::onMusicEvent,
                        onPlaylistEvent = allPlaylistsViewModel::onPlaylistsEvent
                    )

                    TabLayoutComposable(
                        musicState = musicState,
                        playlistsState = playlistState,
                        albumWithArtistState  = albumState,
                        artistState = artistState,
                        onMusicEvent = allMusicsViewModel::onMusicEvent,
                        onPlaylistEvent = allPlaylistsViewModel::onPlaylistsEvent,
                        onAlbumEvent = allAlbumsViewModel::onAlbumEvent,
                        onArtistEvent = allArtistsViewModel::onArtistEvent
                    )
                }
            }
        }
    }
}
