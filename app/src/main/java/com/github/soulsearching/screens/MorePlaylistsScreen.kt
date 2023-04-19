package com.github.soulsearching.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.GridPlaylistComposable
import com.github.soulsearching.viewModels.AllPlaylistsViewModel

@Composable
fun MorePlaylistsScreen(
    allPlaylistsViewModel: AllPlaylistsViewModel,
    navigateToSelectedPlaylist: (String) -> Unit,
    finishAction: () -> Unit
) {

    val playlistState by allPlaylistsViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = stringResource(id = R.string.playlists),
                leftAction = finishAction
            )
        },
        content = { paddingValues ->
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = MaterialTheme.colorScheme.secondary),
                columns = GridCells.Adaptive(196.dp)
            ) {

                items(playlistState.playlists) { playlist ->
                    GridPlaylistComposable(
                        image = playlist.playlistCover,
                        title = playlist.name,
                        text = "small talk...",
                        onClick = {
                            navigateToSelectedPlaylist(playlist.playlistId.toString())
                        }
                    )
                }
            }
        }
    )
}