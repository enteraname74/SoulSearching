package com.github.soulsearching.composables

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.R
import com.github.soulsearching.classes.TabRowItem
import com.github.soulsearching.composables.tabLayoutScreens.AlbumsScreen
import com.github.soulsearching.composables.tabLayoutScreens.ArtistsScreen
import com.github.soulsearching.composables.tabLayoutScreens.MusicsScreen
import com.github.soulsearching.composables.tabLayoutScreens.PlaylistsScreen
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayoutComposable(
    musicState: MusicState,
    playlistsState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    albumWithArtistState: AlbumWithArtistState,
    onAlbumEvent: (AlbumEvent) -> Unit,
    artistState: ArtistState,
    onArtistEvent: (ArtistEvent) -> Unit
) {
    val tabRowItems = listOf(
        TabRowItem(
            title = stringResource(R.string.musics),
            screen = {
                MusicsScreen(
                    state = musicState,
                    onEvent = onMusicEvent
                )
            }
        ),
        TabRowItem(
            title = stringResource(R.string.playlists),
            screen = { PlaylistsScreen(state = playlistsState) }
        ),
        TabRowItem(
            title = stringResource(R.string.albums),
            screen = { AlbumsScreen(state = albumWithArtistState) }
        ),
        TabRowItem(
            title = stringResource(R.string.artists),
            screen = { ArtistsScreen(state = artistState) }
        )
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier
                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    .height(5.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        },
        containerColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        tabRowItems.forEachIndexed { index, item ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            index
                        )
                    }
                },
                text = {
                    Text(
                        text = item.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            )
        }
    }
    HorizontalPager(
        count = tabRowItems.size,
        state = pagerState
    ) {
        tabRowItems[it].screen()
    }
}