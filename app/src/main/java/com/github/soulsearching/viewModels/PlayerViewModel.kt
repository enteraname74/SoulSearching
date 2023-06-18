package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import java.util.*

@SuppressLint("MutableCollectionMutableState")
class PlayerViewModel() : ViewModel() {
    var currentMusic by mutableStateOf<Music?>(null)
    var playlistInfos by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)
    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)
    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)
    var currentMusicCover by mutableStateOf<Bitmap?>(null)
    lateinit var retrieveCoverMethod : (UUID?) -> Bitmap?

    fun setNextMusic() {
        val currentIndex =
            playlistInfos.indexOf(playlistInfos.find { it.musicId == currentMusic!!.musicId })
        currentMusic = playlistInfos[(currentIndex + 1) % playlistInfos.size]
        currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
    }

    fun setPreviousMusic() {
        val currentIndex =
            playlistInfos.indexOf(playlistInfos.find { it.musicId == currentMusic!!.musicId })
        currentMusic = if (currentIndex == 0) {
            playlistInfos.last()
        } else {
            playlistInfos[currentIndex - 1]
        }
        currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
    }

    fun setPlayingState(context : Context) {
        isPlaying = if (isPlaying) {
            PlayerService.pauseMusic(context)
            false
        } else {
            PlayerService.playMusic(context)
            true
        }
    }

    fun setCurrentPlaylistAndMusic(
        music: Music,
        bitmap: Bitmap?,
        playlist: ArrayList<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false,
        context: Context
    ) {
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
                currentMusicCover = bitmap
                if (shouldServiceBeLaunched) {
                    PlayerService.seekToCurrentMusic(context)
                    PlayerService.playMusic(context)
                }
            } else {
                if (shouldServiceBeLaunched) {
                    PlayerService.seekToCurrentMusic(context)
                    PlayerService.playMusic(context)
                }
            }
        } else {
            currentMusic = music
            currentMusicCover = bitmap
            if (shouldServiceBeLaunched) {
                PlayerService.seekToCurrentMusic(context)
                PlayerService.playMusic(context)
            }
        }

        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
        }
    }
}