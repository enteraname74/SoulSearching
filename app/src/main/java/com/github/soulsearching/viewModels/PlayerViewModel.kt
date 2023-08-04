package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.*
import java.util.*

@SuppressLint("MutableCollectionMutableState")
class PlayerViewModel : ViewModel() {
    var currentMusic by mutableStateOf<Music?>(null)
    var currentMusicPosition by mutableStateOf(0)
    var currentPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)
    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)
    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)
    var currentMusicCover by mutableStateOf<Bitmap?>(null)
    lateinit var retrieveCoverMethod: (UUID?) -> Bitmap?
    private var isCounting = false

    fun getIndexOfCurrentMusic(): Int {
        return currentPlaylist.indexOf(currentPlaylist.find { it.musicId == currentMusic!!.musicId })
    }

    fun setNextMusic() {
        val currentIndex =
            currentPlaylist.indexOf(currentPlaylist.find { it.musicId == currentMusic!!.musicId })
        Log.d("Current index :",currentIndex.toString())
        Log.d("Next index :", ((currentIndex + 1) % currentPlaylist.size).toString())
        currentMusic = currentPlaylist[(currentIndex + 1) % currentPlaylist.size]
        currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
    }

    fun setPreviousMusic() {
        val currentIndex =
            currentPlaylist.indexOf(currentPlaylist.find { it.musicId == currentMusic!!.musicId })
        Log.d("Current index :",currentIndex.toString())
        currentMusic = if (currentIndex == 0) {
            Log.d("Prev index :", currentPlaylist.last().toString())
            currentPlaylist.last()
        } else {
            Log.d("Prev index :", (currentIndex - 1).toString())
            currentPlaylist[currentIndex - 1]
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

    fun playShuffle(playlist: ArrayList<Music>) {
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false

        currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
        currentPlaylist.shuffle()
        currentMusic = currentPlaylist[0]
        currentMusicCover = retrieveCoverMethod(currentMusic?.musicId)
        if (shouldServiceBeLaunched) {
            PlayerService.setAndPlayCurrentMusic()
        }

        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
        }
    }

    fun setCurrentPlaylistAndMusic(
        music: Music,
        bitmap: Bitmap?,
        playlist: ArrayList<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false
    ) {
        if (!isMainPlaylist) {
            if (currentPlaylistId == null) {
                currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
                currentPlaylistId = playlistId
                this.isMainPlaylist = false
                if (shouldServiceBeLaunched) {
                    PlayerService.setAndPlayCurrentMusic()
                }
            } else if (currentPlaylistId!!.compareTo(playlistId) != 0) {
                currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
                currentPlaylistId = playlistId
                this.isMainPlaylist = false
                if (shouldServiceBeLaunched) {
                    PlayerService.setAndPlayCurrentMusic()
                }
            }
        } else if (!this.isMainPlaylist) {
            this.isMainPlaylist = true
            currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
            currentPlaylistId = playlistId
            if (shouldServiceBeLaunched) {
                PlayerService.setAndPlayCurrentMusic()
            }
        }

        if (currentMusic != null) {
            if (music.musicId.compareTo(currentMusic!!.musicId) != 0) {
                currentMusic = music
                currentMusicCover = bitmap
                if (shouldServiceBeLaunched) {
                    PlayerService.setAndPlayCurrentMusic()
                    updateCurrentMusicPosition()

                }
            } else {
                if (shouldServiceBeLaunched) {
//                    PlayerService.playMusic()
                }
            }
        } else {
            currentMusic = music
            currentMusicCover = bitmap
            if (shouldServiceBeLaunched) {
                PlayerService.playMusic()
                updateCurrentMusicPosition()
            }
        }
        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
        }

        updateCurrentMusicPosition()
    }

    private fun updateCurrentMusicPosition() {
        isCounting = false
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                isCounting = true
                while(isCounting) {
                    Thread.sleep(1000)
                    currentMusicPosition = PlayerService.getCurrentMusicPosition()
                }
            }
        }
    }

    fun resetPlayerData() {
        currentMusic = null
        currentMusicCover = null
        currentPlaylist = ArrayList()
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false
        isServiceLaunched = false
        shouldServiceBeLaunched = false
        isCounting = false
    }

    fun getNextMusic(): Music {
        val currentIndex =
            currentPlaylist.indexOf(currentPlaylist.find { it.musicId == currentMusic!!.musicId })
        Log.d("CURRENT INDEX BEFORE NEXT", currentIndex.toString())
        Log.d("CURRENT INDEX AFTER NEXT", ((currentIndex + 1) % currentPlaylist.size).toString())
        return currentPlaylist[(currentIndex + 1) % currentPlaylist.size]
    }

    fun getPreviousMusic(): Music {
        val currentIndex =
            currentPlaylist.indexOf(currentPlaylist.find { it.musicId == currentMusic!!.musicId })

        Log.d("CURRENT INDEX BEFORE PREV", currentIndex.toString())
        return if (currentIndex == 0) {
            Log.d("CURRENT INDEX BEFORE PREV", currentPlaylist.last().toString())
            currentPlaylist.last()
        } else {
            Log.d("CURRENT INDEX BEFORE PREV", (currentIndex-1).toString())
            currentPlaylist[currentIndex - 1]
        }
    }
}