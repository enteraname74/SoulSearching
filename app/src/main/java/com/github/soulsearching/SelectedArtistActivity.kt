package com.github.soulsearching

import android.content.Intent
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
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.SelectedArtistViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SelectedArtistActivity : ComponentActivity() {
    private val viewModel: SelectedArtistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoulSearchingTheme {
                val artistState by viewModel.selectedArtistState.collectAsState()
                val musicState by viewModel.musicState.collectAsState()

                var isArtistFetched by rememberSaveable {
                    mutableStateOf(false)
                }
                if (!isArtistFetched) {
                    viewModel.setSelectedArtist(UUID.fromString(intent.extras?.getString("artistId")))
                    isArtistFetched = true
                }

                PlaylistScreen(
                    onMusicEvent = viewModel::onMusicEvent,
                    musicState = musicState,
                    title = artistState.artistWithMusics.artist.artistName,
                    image = artistState.artistWithMusics.artist.artistCover,
                    editAction = {
                        val intent = Intent(applicationContext, ModifyArtistActivity::class.java)
                        intent.putExtra(
                            "artistId",
                            artistState.artistWithMusics.artist.artistId.toString()
                        )
                        startActivity(intent)
                    }
                )
            }
        }
    }
}