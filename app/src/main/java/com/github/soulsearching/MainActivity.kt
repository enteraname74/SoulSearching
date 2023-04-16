package com.github.soulsearching

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.composables.*
import com.github.soulsearching.composables.bottomSheets.AddToPlaylistBottomSheet
import com.github.soulsearching.composables.bottomSheets.MusicBottomSheetMenu
import com.github.soulsearching.composables.dialogs.DeleteMusicDialog
import com.github.soulsearching.composables.screens.TestButtons
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.AllAlbumsViewModel
import com.github.soulsearching.viewModels.AllArtistsViewModel
import com.github.soulsearching.viewModels.AllMusicsViewModel
import com.github.soulsearching.viewModels.AllPlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val allMusicsViewModel: AllMusicsViewModel by viewModels()
    private val allPlaylistsViewModel: AllPlaylistsViewModel by viewModels()
    private val allAlbumsViewModel: AllAlbumsViewModel by viewModels()
    private val allArtistsViewModel: AllArtistsViewModel by viewModels()

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val musicState by allMusicsViewModel.state.collectAsState()
            val playlistState by allPlaylistsViewModel.state.collectAsState()
            val albumState by allAlbumsViewModel.state.collectAsState()
            val artistState by allArtistsViewModel.state.collectAsState()

            val coroutineScope = rememberCoroutineScope()
            val musicModalSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
            val addToPlaylistModalSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

            BackHandler(addToPlaylistModalSheetState.isVisible) {
                coroutineScope.launch { addToPlaylistModalSheetState.hide() }
            }

            BackHandler(musicModalSheetState.isVisible) {
                Log.d("STOP VISIBLE", "STOP VISIBLE")
                coroutineScope.launch { musicModalSheetState.hide() }
            }

            SoulSearchingTheme {
                if (musicState.isDeleteDialogShown) {
                    DeleteMusicDialog(
                        onMusicEvent = allMusicsViewModel::onMusicEvent,
                        confirmAction = {
                            coroutineScope.launch { musicModalSheetState.hide() }
                                .invokeOnCompletion {
                                    if (!musicModalSheetState.isVisible) {
                                        allMusicsViewModel.onMusicEvent(
                                            MusicEvent.BottomSheet(
                                                isShown = false
                                            )
                                        )
                                    }
                                }
                        }
                    )
                }

                if (musicState.isBottomSheetShown) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            allMusicsViewModel.onMusicEvent(
                                MusicEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                        },
                        sheetState = musicModalSheetState,
                        dragHandle = {}
                    ) {
                        MusicBottomSheetMenu(
                            modifyAction = {
                                coroutineScope.launch { musicModalSheetState.hide() }
                                    .invokeOnCompletion {
                                        if (!musicModalSheetState.isVisible) {
                                            allMusicsViewModel.onMusicEvent(
                                                MusicEvent.BottomSheet(
                                                    isShown = false
                                                )
                                            )
                                        }
                                    }

                                val intent =
                                    Intent(applicationContext, ModifyMusicActivity::class.java)
                                intent.putExtra(
                                    "musicId",
                                    musicState.selectedMusic.musicId.toString()
                                )
                                startActivity(intent)
                            },
                            removeAction = {
                                allMusicsViewModel.onMusicEvent(MusicEvent.DeleteDialog(isShown = true))
                            },
                            addToPlaylistAction = {
                                allMusicsViewModel.onMusicEvent(
                                    MusicEvent.AddToPlaylistBottomSheet(
                                        isShown = true
                                    )
                                )
                            }
                        )
                    }
                }

                if (musicState.isAddToPlaylistDialogShown) {
                    ModalBottomSheet(
                        modifier = Modifier.fillMaxSize(),
                        onDismissRequest = {
                            allMusicsViewModel.onMusicEvent(
                                MusicEvent.AddToPlaylistBottomSheet(
                                    isShown = false
                                )
                            )
                        },
                        sheetState = musicModalSheetState,
                        dragHandle = {}
                    ) {
                        AddToPlaylistBottomSheet(
                            playlistState = playlistState,
                            onPlaylistEvent = allPlaylistsViewModel::onPlaylistsEvent,
                            cancelAction = {
                                coroutineScope.launch { addToPlaylistModalSheetState.hide() }
                                    .invokeOnCompletion {
                                        if (!addToPlaylistModalSheetState.isVisible) {
                                            allMusicsViewModel.onMusicEvent(
                                                MusicEvent.AddToPlaylistBottomSheet(
                                                    isShown = false
                                                )
                                            )
                                        }
                                    }
                            },
                            validationAction = {}
                        )
                    }
                }

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
                                            startActivity(
                                                Intent(
                                                    applicationContext,
                                                    MorePlaylistsActivity::class.java
                                                )
                                            )
                                        }
                                    )
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = Constants.Spacing.medium,
                                                end = Constants.Spacing.medium
                                            ),
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
                                            startActivity(
                                                Intent(
                                                    applicationContext,
                                                    MoreAlbumsActivity::class.java
                                                )
                                            )
                                        }
                                    )
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = Constants.Spacing.medium,
                                                end = Constants.Spacing.medium
                                            ),
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
                                            startActivity(
                                                Intent(
                                                    applicationContext,
                                                    MoreArtistsActivity::class.java
                                                )
                                            )
                                        }
                                    )
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = Constants.Spacing.medium,
                                                end = Constants.Spacing.medium
                                            ),
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
                                MusicSubMenuComposable(
                                    shuffleAction = {}
                                )
                            }
                            items(musicState.musics) { music ->
                                Row(Modifier.animateItemPlacement()) {
                                    MusicItemComposable(
                                        music = music,
                                        onClick = allMusicsViewModel::onMusicEvent,
                                        onLongClick = {
                                            coroutineScope.launch {
                                                allMusicsViewModel.onMusicEvent(
                                                    MusicEvent.SetSelectedMusic(
                                                        music
                                                    )
                                                )
                                                allMusicsViewModel.onMusicEvent(
                                                    MusicEvent.BottomSheet(
                                                        isShown = true
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

