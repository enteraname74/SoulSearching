package com.github.soulsearching

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.soulsearching.composables.*
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

    @OptIn(ExperimentalFoundationApi::class)
    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val musicState by allMusicsViewModel.state.collectAsState()
            val playlistState by allPlaylistsViewModel.state.collectAsState()
            val albumState by allAlbumsViewModel.state.collectAsState()
            val artistState by allArtistsViewModel.state.collectAsState()

            SoulSearchingTheme {
                Scaffold(
                    topBar = { MainMenuHeaderComposable() },
                    content = { paddingValues ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                ) {
                                    SubMenuComposable(
                                        title = stringResource(id = R.string.shortcuts),
                                        moreAction = {}
                                    )
                                    TestButtons(
                                        onMusicEvent = allMusicsViewModel::onMusicEvent,
                                        onPlaylistEvent = allPlaylistsViewModel::onPlaylistsEvent
                                    )
                                }
                            }
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                ) {
                                    SubMenuComposable(
                                        title = stringResource(id = R.string.playlists),
                                        moreAction = {
                                            startActivity(Intent(applicationContext, MorePlaylistsActivity::class.java))
                                        }
                                    )
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = Constants.Spacing.medium, end = Constants.Spacing.medium),
                                        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
                                    ) {
                                        items(playlistState.playlists) { playlist ->
                                            LazyRowComposable(
                                                image = playlist.playlistCover,
                                                title = playlist.name,
                                                text = "",
                                                onClick = {
                                                    val intent = Intent(
                                                        applicationContext,
                                                        SelectedPlaylistActivity::class.java
                                                    )
                                                    intent.putExtra(
                                                        "playlistId",
                                                        playlist.playlistId.toString()
                                                    )
                                                    startActivity(intent)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                ) {
                                    SubMenuComposable(
                                        title = stringResource(id = R.string.albums),
                                        moreAction = {
                                            startActivity(Intent(applicationContext, MoreAlbumsActivity::class.java))
                                        }
                                    )
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = Constants.Spacing.medium, end = Constants.Spacing.medium),
                                        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
                                    ) {
                                        items(albumState.albums) { albumWithArtist ->
                                            LazyRowComposable(
                                                image = albumWithArtist.album.albumCover,
                                                title = albumWithArtist.album.albumName,
                                                text = if (albumWithArtist.artist != null) albumWithArtist.artist.artistName else "",
                                                onClick = {
                                                    val intent = Intent(
                                                        applicationContext,
                                                        SelectedAlbumActivity::class.java
                                                    )
                                                    intent.putExtra(
                                                        "albumId",
                                                        albumWithArtist.album.albumId.toString()
                                                    )
                                                    startActivity(intent)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                ) {
                                    SubMenuComposable(
                                        title = stringResource(id = R.string.artists),
                                        moreAction = {
                                            startActivity(Intent(applicationContext, MoreArtistsActivity::class.java))
                                        }
                                    )
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = Constants.Spacing.medium, end = Constants.Spacing.medium),
                                        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
                                    ) {
                                        items(artistState.artists) { artist ->
                                            LazyRowComposable(
                                                image = artist.artistCover,
                                                title = artist.artistName,
                                                text = "",
                                                onClick = {
                                                    val intent = Intent(
                                                        applicationContext,
                                                        SelectedArtistActivity::class.java
                                                    )
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
                            }
                            stickyHeader {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(Constants.Spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.musics),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                            items(musicState.musics) { music ->
                                MusicItemComposable(
                                    music = music,
                                    onClick = allMusicsViewModel::onMusicEvent,
                                    onLongClick = {
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

