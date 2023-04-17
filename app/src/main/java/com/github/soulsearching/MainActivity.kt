package com.github.soulsearching

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.composables.*
import com.github.soulsearching.composables.bottomSheets.AddToPlaylistBottomSheet
import com.github.soulsearching.composables.bottomSheets.MusicBottomSheet
import com.github.soulsearching.composables.bottomSheets.TestBottomSheet
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

@Suppress("UNCHECKED_CAST")
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
                    MusicBottomSheet(
                        onMusicEvent = allMusicsViewModel::onMusicEvent,
                        onPlaylistEvent = allPlaylistsViewModel::onPlaylistsEvent,
                        musicModalSheetState = musicModalSheetState,
                        musicState = musicState
                    )
                }

                if (musicState.isAddToPlaylistDialogShown) {
                    AddToPlaylistBottomSheet(
                        selectedMusicId = musicState.selectedMusic.musicId,
                        onMusicEvent = allMusicsViewModel::onMusicEvent,
                        onPlaylistsEvent = allPlaylistsViewModel::onPlaylistsEvent,
                        musicModalSheetState = musicModalSheetState,
                        addToPlaylistModalSheetState = addToPlaylistModalSheetState,
                        playlistState = playlistState
                    )
                }
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val constraintsScope = this
                    val maxHeight = with(LocalDensity.current) {
                        constraintsScope.maxHeight.toPx()
                    }
                    Scaffold(
                        topBar = { MainMenuHeaderComposable() },
                        content = { paddingValues ->
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(paddingValues)
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
                                    MainMenuLazyListRow(
                                        list = playlistState.playlists,
                                        title = stringResource(id = R.string.playlists),
                                        moreActivity = MorePlaylistsActivity::class.java as Class<Any>,
                                        deleteAlbumAction = allAlbumsViewModel::onAlbumEvent
                                    )
                                }
                                item {
                                    MainMenuLazyListRow(
                                        list = albumState.albums,
                                        title = stringResource(id = R.string.albums),
                                        moreActivity = MoreAlbumsActivity::class.java as Class<Any>,
                                        deleteAlbumAction = allAlbumsViewModel::onAlbumEvent
                                    )
                                }
                                item {
                                    MainMenuLazyListRow(
                                        list = artistState.artists,
                                        title = stringResource(id = R.string.artists),
                                        moreActivity = MoreArtistsActivity::class.java as Class<Any>,
                                        deleteAlbumAction = allAlbumsViewModel::onAlbumEvent
                                    )
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
                    TestBottomSheet(maxHeight = maxHeight)
                }
            }
        }
    }
}