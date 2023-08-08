package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.github.soulsearching.classes.ColorPaletteUtils
import com.github.soulsearching.classes.PlayerMode
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.*
import java.lang.Integer.max
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("MutableCollectionMutableState")
class PlayerViewModel : ViewModel() {
    var currentMusic by mutableStateOf<Music?>(null)
    var currentMusicPosition by mutableStateOf(0)
    var currentMusicCover by mutableStateOf<Bitmap?>(null)
    var currentColorPalette by mutableStateOf<Palette.Swatch?>(null)

    var initialPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)

    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)

    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)

    var playerMode by mutableStateOf(PlayerMode.NORMAL)

    lateinit var retrieveCoverMethod: (UUID?) -> Bitmap?

    private var isCounting = false
    private var isChangingPlayMode = false

    fun getIndexOfCurrentMusic(): Int {
        return if (currentMusic == null) {
            -1
        } else {
            currentPlaylist.indexOf(currentPlaylist.find { it.musicId == currentMusic!!.musicId })
        }
    }

    fun setPlayerInformationsFromSavedList(musicList: ArrayList<Music>) {
        currentPlaylist = musicList.map { it.copy() } as ArrayList<Music>
        initialPlaylist = musicList.map { it.copy() } as ArrayList<Music>

        SharedPrefUtils.getPlayerSavedCurrentMusic()
        currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
        currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)
    }

    fun setNextMusic() {
        if (currentPlaylist.size != 0) {
            val currentIndex = getIndexOfCurrentMusic()

            currentMusic = currentPlaylist[(currentIndex + 1) % currentPlaylist.size]
            currentMusicPosition = 0
            currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
            currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)
        }
    }

    fun setPreviousMusic() {
        val currentIndex = getIndexOfCurrentMusic()

        currentMusic = if (currentIndex == 0) {
            currentPlaylist.last()
        } else {
            currentPlaylist[currentIndex - 1]
        }
        currentMusicPosition = 0
        currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
        currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)
    }

    fun setPlayingState() {
        PlayerService.togglePlayPause()
        isPlaying = PlayerService.isPlayerPlaying()
    }

    fun playShuffle(playlist: ArrayList<Music>) {
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false

        currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
        currentPlaylist.shuffle()
        playerMode = PlayerMode.SHUFFLE

        currentMusic = currentPlaylist[0]
        currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
        currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)

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
                currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)
                if (shouldServiceBeLaunched) {
                    PlayerService.setAndPlayCurrentMusic()
                }
            }
        } else {
            currentMusic = music
            currentMusicCover = bitmap
            currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)
            if (shouldServiceBeLaunched) {
                PlayerService.playMusic()
            }
        }
        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
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

    fun setMusicFromIndex(index: Int) {
        if (currentPlaylist.lastIndex != -1 ){
            currentMusic = if (index <= currentPlaylist.lastIndex) {
                currentPlaylist[max(0,index)]
            } else {
                currentPlaylist[0]
            }
        }
    }

    fun resetPlayerData() {
        currentMusic = null
        currentMusicCover = null
        currentColorPalette = null
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