package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun MusicBottomSheetScreen(
    viewModel: MusicBottomSheetViewModel,
) {
    val state: MusicBottomSheetState by viewModel.state.collectAsStateWithLifecycle()
    state.dialogState?.Dialog()

    MusicBottomSheetMenu(
        topInformation = state.bottomSheetTopInformation,
        itemsVisibility = state.itemsVisibility,
        modifyAction = viewModel::modifyMusic,
        quickAccessAction = viewModel::handleQuickAccess,
        removeAction = viewModel::showDeleteDialog,
        addToPlaylistAction = viewModel::addToPlaylists,
        removeFromPlaylistAction = viewModel::showRemoveFromPlaylistDialog,
        removeFromPlayedListAction = viewModel::removeFromPlayedList,
        playNextAction = viewModel::playNext,
        addToQueueAction = viewModel::addToQueue,
        deleteText = if (state.musics.size > 1) {
            strings.deleteSelectedMusics
        } else {
            strings.deleteMusic
        }
    )
}