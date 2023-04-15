package com.github.soulsearching

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.GridPlaylistComposable
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.AllArtistsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreArtistsActivity : ComponentActivity() {
    private val allArtistsViewModel : AllArtistsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by allArtistsViewModel.state.collectAsState()

            SoulSearchingTheme {
                Scaffold(
                    topBar = {
                        AppHeaderBar(
                            title = stringResource(id = R.string.albums),
                            leftAction = { finish() })
                    },
                    content = { paddingValues ->
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .background(color = MaterialTheme.colorScheme.secondary),
                            columns = GridCells.Adaptive(196.dp)
                        ) {
                            items(state.artists) { artist ->
                                GridPlaylistComposable(
                                    image = artist.artistCover,
                                    title = artist.artistName,
                                    text = "small talk...",
                                    onClick = {
                                        val intent = Intent(applicationContext, SelectedArtistActivity::class.java)
                                        intent.putExtra(
                                            "artistId",
                                            artist.artistId.toString()
                                        )
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
