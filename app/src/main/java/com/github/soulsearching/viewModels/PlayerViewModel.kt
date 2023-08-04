package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.PlayerMode
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.*
import java.util.*

@SuppressLint("MutableCollectionMutableState")
class PlayerViewModel : ViewModel() {
    var currentMusic by mutableStateOf<Music?>(null)
    var currentMusicPosition by mutableStateOf(0)
    var currentMusicCover by mutableStateOf<Bitmap?>(null)

    var initialPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)

    private var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)

    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)

    var playerMode by mutableStateOf(PlayerMode.NORMAL)

    lateinit var retrieveCoverMethod: (UUID?) -> Bitmap?

    private var isCounting = false
    private var isChangingPlayMode = false

    fun getIndexOfCurrentMusic(): Int {
        return currentPlaylist.indexOf(currentPlaylist.find { it.musicId == currentMusic!!.musicId })
    }

    fun setNextMusic() {
        if (currentPlaylist.size != 0) {
            val currentIndex = getIndexOfCurrentMusic()

            currentMusic = currentPlaylist[(currentIndex + 1) % currentPlaylist.size]
            currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
        }
    }

    fun setPreviousMusic() {
        val currentIndex = getIndexOfCurrentMusic()

        currentMusic = if (currentIndex == 0) {
            currentPlaylist.last()
        } else {
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
        playerMode = PlayerMode.SHUFFLE

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
                initialPlaylist = playlist.map { it.copy() } as ArrayList<Music>
                currentPlaylistId = playlistId
                this.isMainPlaylist = false
                if (shouldServiceBeLaunched) {
                    PlayerService.setAndPlayCurrentMusic()
                }
            } else if (currentPlaylistId!!.compareTo(playlistId) != 0) {
                currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
                initialPlaylist = playlist.map { it.copy() } as ArrayList<Music>
                currentPlaylistId = playlistId
                this.isMainPlaylist = false
                if (shouldServiceBeLaunched) {
                    PlayerService.setAndPlayCurrentMusic()
                }
            }
        } else if (!this.isMainPlaylist) {
            this.isMainPlaylist = true
            currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
            initialPlaylist = playlist.map { it.copy() } as ArrayList<Music>
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

    fun changePlayerMode() {
        if (!isChangingPlayMode) {
            isChangingPlayMode = true
            when(playerMode) {
                PlayerMode.NORMAL -> {
                    currentPlaylist = initialPlaylist.map { it.copy() } as ArrayList<Music>
                    currentPlaylist.shuffle()
                    playerMode = PlayerMode.SHUFFLE
                }
                PlayerMode.SHUFFLE -> {
                    currentPlaylist = arrayListOf(currentMusic!!)
                    playerMode = PlayerMode.LOOP
                }
                PlayerMode.LOOP -> {
                    currentPlaylist = initialPlaylist.map { it.copy() } as ArrayList<Music>
                    playerMode = PlayerMode.NORMAL
                }
            }
            isChangingPlayMode = false
        }
    }

    fun resetPlayerData() {
        currentMusic = null
        currentMusicCover = null
        playerMode = PlayerMode.NORMAL
        initialPlaylist = ArrayList()
        currentPlaylist = ArrayList()
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false
        isServiceLaunched = false
        shouldServiceBeLaunched = false
        isCounting = false
    }
}