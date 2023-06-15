package com.github.soulsearching.classes

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.viewModels.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class PlayerUtils {
    companion object {
        val playerViewModel = PlayerViewModel()

        @OptIn(ExperimentalMaterialApi::class)
        fun setCurrentPlaylistAndMusic(
            swipeableState: SwipeableState<BottomSheetStates>,
            music: Music,
            playlist: ArrayList<Music>,
            playlistId: UUID?,
            isMainPlaylist: Boolean = false,
            coroutineScope: CoroutineScope
        ) {
            coroutineScope.launch {
                swipeableState.animateTo(BottomSheetStates.EXPANDED)
            }
            if (
                playerViewModel.currentPlaylistId?.compareTo(playlistId) != 0) {
                playerViewModel.currentMusic = music
                playerViewModel.playlistInfos = playlist
                playerViewModel.currentPlaylistId = playlistId
            }
        }
    }
}