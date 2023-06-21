package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import java.util.*
import kotlin.collections.ArrayList

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
    lateinit var uri: Uri
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

    fun setPlayingState() {
        isPlaying = if (isPlaying) {
            PlayerService.pauseMusic()
            false
        } else {
            PlayerService.playMusic()
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
            currentMusicCover = bitmap
            if (shouldServiceBeLaunched) {
                PlayerService.seekToCurrentMusic()
                PlayerService.playMusic()
            }
        }

        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
        }
    }

    fun updateCurrentMusicFromUUID(musicId: UUID) {
        currentMusic = playlistInfos.find {
            it.musicId == musicId
        }
        currentMusicCover = retrieveCoverMethod(currentMusic?.coverId)
    }

    fun resetPlayerData() {
        currentMusic = null
        currentMusicCover = null
        playlistInfos = ArrayList()
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false
    }

    fun getNextMusic() : Music {
        val currentIndex =
            playlistInfos.indexOf(playlistInfos.find { it.musicId == currentMusic!!.musicId })
        Log.d("CURRENT INDEX BEFORE NEXT", currentIndex.toString())
        return playlistInfos[(currentIndex + 1) % playlistInfos.size]
    }

    fun getPreviousMusic() : Music {
        val currentIndex =
            playlistInfos.indexOf(playlistInfos.find { it.musicId == currentMusic!!.musicId })

        Log.d("CURRENT INDEX BEFORE PREV", currentIndex.toString())
        return if (currentIndex == 0) {
            playlistInfos.last()
        } else {
            playlistInfos[currentIndex - 1]
        }
    }
}