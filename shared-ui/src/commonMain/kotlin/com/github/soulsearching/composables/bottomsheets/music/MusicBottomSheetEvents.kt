package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel

@Composable
@OptIn(ExperimentalMaterialApi::class)
expect fun MusicBottomSheetEvents(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistsEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    secondaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    onPrimaryColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    onSecondaryColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    playbackManager: PlaybackManager = injectElement()
)