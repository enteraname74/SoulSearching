package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.github.soulsearching.classes.ColorPaletteUtils
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
    var currentColorPalette by mutableStateOf<Palette.Swatch?>(null)

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

    fun setNextMusic(context: Context) {
        if (currentPlaylist.size != 0) {
            val currentIndex = getIndexOfCurrentMusic()

            currentMusic = currentPlaylist[(currentIndex + 1) % currentPlaylist.size]
            currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
            currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover, context)
        }
    }

    fun setPreviousMusic(context: Context) {
        val currentIndex = getIndexOfCurrentMusic()

        currentMusic = if (currentIndex == 0) {
            currentPlaylist.last()
        } else {
            currentPlaylist[currentIndex - 1]
        }
        currentMusicCover = retrieveCoverMethod(currentMusic!!.coverId)
        currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover, context)
    }

    fun setPlayingState() {
        PlayerService.pauseMusic()
        isPlaying = PlayerService.isPlayerPlaying()
    }

    fun playShuffle(playlist: ArrayList<Music>, context: Context) {
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false

        currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
        currentPlaylist.shuffle()
        playerMode = PlayerMode.SHUFFLE

        currentMusic = currentPlaylist[0]
        currentMusicCover = retrieveCoverMethod(currentMusic?.musicId)
        currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover, context)

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
        isMainPlaylist: Boolean = false,
        context: Context
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
                currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover, context)
                if (shouldServiceBeLaunched) {
                    PlayerService.setAndPlayCurrentMusic()

                }
            }
        } else {
            currentMusic = music
            currentMusicCover = bitmap
            currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover, context)
            if (shouldServiceBeLaunched) {
                PlayerService.playMusic()
            }
        }
        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
        }
    }

    fun updateCurrentMusicPosition() {
        isCounting = false
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                isCounting = true
                while(isCounting) {
                    Thread.sleep(1000)
                    currentMusicPosition = PlayerService.getCurrentMusicPosition()
                }
                Log.d("PLAYER VIEW MODEL", "END OF COUNT")
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