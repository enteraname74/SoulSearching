package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MusicBottomSheetScreen(
    viewModel: MusicBottomSheetViewModel,
) {
    val state: MusicBottomSheetState by viewModel.state.collectAsStateWithLifecycle()

    state.bottomSheetState?.BottomSheet()
    state.dialogState?.Dialog()

    state.selectedMusic?.let {
        MusicBottomSheetMenu(
            selectedMusic = it,
            musicBottomSheetMode = state.mode,
            modifyAction = viewModel::modifyMusic,
            quickAccessAction = viewModel::toggleQuickAccess,
            removeAction = viewModel::showDeleteDialog,
            addToPlaylistAction = viewModel::addToPlaylists,
            removeFromPlaylistAction = viewModel::showRemoveFromPlaylistDialog,
            removeFromPlayedListAction = viewModel::removeFromPlayedList,
            playNextAction = viewModel::playNext,
            addToQueueAction = viewModel::addToQueue,
            isCurrentlyPlaying = state.isCurrentlyPlaying,
            isInPlayedList = state.isInPlayedList,
        )
    }
}