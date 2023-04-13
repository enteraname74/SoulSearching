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
import com.github.soulsearching.viewModels.SelectedAlbumViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SelectedAlbumActivity : ComponentActivity() {
    private val viewModel : SelectedAlbumViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoulSearchingTheme {
                val albumState by viewModel.selectedAlbumState.collectAsState()
                val musicState by viewModel.musicState.collectAsState()

                var isAlbumFetched by rememberSaveable {
                    mutableStateOf(false)
                }
                if (!isAlbumFetched) {
                    viewModel.setSelectedAlbum(UUID.fromString(intent.extras?.getString("albumId")))
                    isAlbumFetched = true
                }

                PlaylistScreen(
                    onMusicEvent = viewModel::onMusicEvent,
                    musicState = musicState,
                    title = albumState.albumWithMusics.album.name,
                    image = albumState.albumWithMusics.album.albumCover,
                    editAction = {
                        val intent = Intent(applicationContext, ModifyAlbumActivity::class.java)
                        intent.putExtra(
                            "albumId",
                            albumState.albumWithMusics.album.albumId.toString()
                        )
                        startActivity(intent)
                    }
                )
            }
        }
    }
}