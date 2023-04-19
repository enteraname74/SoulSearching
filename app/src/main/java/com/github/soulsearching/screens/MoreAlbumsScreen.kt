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
import com.github.soulsearching.viewModels.AllAlbumsViewModel

@Composable
fun MoreAlbumsScreen(
    allAlbumsViewModel: AllAlbumsViewModel,
    navigateToSelectedAlbum: (String) -> Unit,
    finishAction: () -> Unit
) {

    val albumState by allAlbumsViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = stringResource(id = R.string.albums),
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
                items(albumState.albums) { albumWithArtist ->
                    GridPlaylistComposable(
                        image = albumWithArtist.album.albumCover,
                        title = albumWithArtist.album.albumName,
                        text = if (albumWithArtist.artist != null) albumWithArtist.artist.artistName else "",
                        onClick = { navigateToSelectedAlbum(albumWithArtist.album.albumId.toString()) }
                    )
                }
            }
        }
    )
}
