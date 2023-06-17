package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("MutableCollectionMutableState")
class PlayerViewModel : ViewModel() {
    var currentMusic by mutableStateOf<Music?>(null)
    var playlistInfos by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)
    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)
    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)

    fun setNextMusic() {
        val currentIndex =
            playlistInfos.indexOf(playlistInfos.find { it.musicId == currentMusic!!.musicId })
        currentMusic = playlistInfos[(currentIndex + 1) % playlistInfos.size]
    }

    fun setPreviousMusic() {
        val currentIndex =
            playlistInfos.indexOf(playlistInfos.find { it.musicId == currentMusic!!.musicId })
        currentMusic = if (currentIndex == 0) {
            playlistInfos.last()
        } else {
            playlistInfos[currentIndex - 1]
        }
    }

    fun setPlayingState() {
        if (isPlaying) {
            PlayerService.pauseMusic()
            isPlaying = false
        } else {
            PlayerService.playMusic()
            isPlaying = true
        }
    }

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

        if (!isMainPlaylist) {
            if (currentPlaylistId == null) {
                playlistInfos = playlist
                currentPlaylistId = playlistId
                this.isMainPlaylist = false
                if (shouldServiceBeLaunched) {
                    PlayerService.setPlayerPlaylist()
                }
            } else if(currentPlaylistId!!.compareTo(playlistId) != 0) {
                playlistInfos = playlist
                currentPlaylistId = playlistId
                this.isMainPlaylist = false
                if (shouldServiceBeLaunched) {
                    PlayerService.setPlayerPlaylist()
                }
            }
        } else if (!this.isMainPlaylist){
            this.isMainPlaylist = true
            playlistInfos = playlist
            currentPlaylistId = playlistId
            if (shouldServiceBeLaunched) {
                PlayerService.setPlayerPlaylist()
            }
        }

        if (currentMusic != null){
            if (music.musicId.compareTo(currentMusic!!.musicId) != 0) {
                currentMusic = music
                if (shouldServiceBeLaunched) {
                    PlayerService.seekToCurrentMusic()
                    PlayerService.playMusic()
                }
            } else {
                if (shouldServiceBeLaunched) {
                    PlayerService.seekToCurrentMusic()
                    PlayerService.playMusic()
                }
            }
        } else {
            currentMusic = music
            if (shouldServiceBeLaunched) {
                PlayerService.seekToCurrentMusic()
                PlayerService.playMusic()
            }
        }

        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
        }
    }
}