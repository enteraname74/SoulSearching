package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.github.soulsearching.classes.*
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.*
import java.lang.Integer.max
import java.util.*

@SuppressLint("MutableCollectionMutableState")
class PlayerViewModel : ViewModel() {
    var currentMusic by mutableStateOf<Music?>(null)
    var currentMusicPosition by mutableStateOf(0)
    var currentMusicCover by mutableStateOf<Bitmap?>(null)
    var currentColorPalette by mutableStateOf<Palette.Swatch?>(null)

    private var initialPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)

    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)

    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)

    var playerMode by mutableStateOf(PlayerMode.NORMAL)

    lateinit var retrieveCoverMethod: (UUID?) -> Bitmap?
    lateinit var updateNbPlayed: (UUID) -> Unit

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

    fun isSameMusic(musicId: UUID): Boolean {
        Log.d("VM", "SAME MUSIC")
        return if (currentMusic == null) {
            false
        } else {
            currentMusic!!.musicId.compareTo(musicId) == 0
        }
    }

    fun isSamePlaylist(isMainPlaylist: Boolean, playlistId: UUID?): Boolean {
        if (playlistId == null && this.currentPlaylistId == null) {
            return isMainPlaylist == this.isMainPlaylist
        } else if (playlistId != null && this.currentPlaylistId != null) {
            return (playlistId.compareTo(currentPlaylistId) == 0) && (isMainPlaylist == this.isMainPlaylist)
        }
        return false
    }

    fun updateMusic(music: Music) {
        CoroutineScope(Dispatchers.IO).launch {
            currentMusic?.let {
                if (it.musicId == music.musicId) {
                   setNewCurrentMusicInformation(music)
                    PlayerService.updateNotification()
                }
            }

            val indexCurrent = currentPlaylist.indexOfFirst { it.musicId == music.musicId }
            if (indexCurrent != -1) {
                currentPlaylist[indexCurrent] = music
            }

            val indexInitial = initialPlaylist.indexOfFirst { it.musicId == music.musicId }
            if (indexInitial != -1) {
                initialPlaylist[indexInitial] = music
            }
        }
    }

    fun setNewCurrentMusicInformation(music: Music?) {
        currentMusic = music
        currentMusicPosition = 0
        currentMusicCover = retrieveCoverMethod(currentMusic?.coverId)
        currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)
    }

    fun addMusicToPlayNext(
        music: Music,
        context: Context
    ) {
        // If same music than the one played, does nothing :
        currentMusic?.let {
            if (music.musicId.compareTo(it.musicId) == 0) {
                return
            }
        }
        // If the current playlist is empty, we load the music :
        if (currentPlaylist.isEmpty()) {
            currentPlaylist.add(music)
            setNewCurrentMusicInformation(music)
            Utils.launchService(
                context = context,
                isFromSavedList = true
            )
        }

        // We make sure to remove the music if it's already in the playlist :
        currentPlaylist.removeIf { it.musicId == music.musicId }

        // Finally, we add the new next music :
        val currentIndex = getIndexOfCurrentMusic()
        currentPlaylist.add(currentIndex + 1, music)

        SharedPrefUtils.setPlayerSavedCurrentMusic()
    }

    fun removeMusicIfSamePlaylist(musicId: UUID, context: Context, playlistId: UUID?) {
        if (playlistId == null && currentPlaylistId == null) {
            removeMusicFromCurrentPlaylist(musicId, context)
        } else if (playlistId != null && currentPlaylistId != null) {
            if (playlistId.compareTo(currentPlaylistId) == 0) {
                removeMusicFromCurrentPlaylist(musicId, context)
            }
        }
    }

    fun removeMusicFromCurrentPlaylist(musicId: UUID, context: Context) {
        val currentIndex = getIndexOfCurrentMusic()
        currentPlaylist.removeIf { it.musicId == musicId }

        // If no songs is left in the queue, stop playing :
        if (currentPlaylist.isEmpty()) {
            PlayerService.stopMusic(context)
        } else {
            // If same music than the one played, play next song :
            currentMusic?.let {
                if (it.musicId.compareTo(musicId) == 0) {
                    // We place ourself in the previous music :
                    currentMusic = currentPlaylist[(currentIndex) % currentPlaylist.size]

                    PlayerService.setAndPlayCurrentMusic()
                }
            }
        }
        SharedPrefUtils.setPlayerSavedCurrentMusic()
    }

    fun setNextMusic() {
        if (currentPlaylist.size != 0) {
            val currentIndex = getIndexOfCurrentMusic()

            setNewCurrentMusicInformation(getNextMusic(currentIndex))

            SharedPrefUtils.setPlayerSavedCurrentMusic()
        }
    }

    private fun getNextMusic(currentIndex: Int): Music? {
        return if (currentPlaylist.isNotEmpty()) currentPlaylist[(currentIndex + 1) % currentPlaylist.size] else null
    }

    private fun getPreviousMusic(currentIndex: Int): Music? {
        return if (currentPlaylist.isNotEmpty()) {
            if (currentIndex == 0) {
                currentPlaylist.last()
            } else {
                currentPlaylist[currentIndex - 1]
            }
        } else {
            null
        }
    }

    fun setPreviousMusic() {
        val currentIndex = getIndexOfCurrentMusic()

        setNewCurrentMusicInformation(getPreviousMusic(currentIndex))

        SharedPrefUtils.setPlayerSavedCurrentMusic()
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

        setNewCurrentMusicInformation(currentPlaylist[0])

        if (shouldServiceBeLaunched) {
            PlayerService.setAndPlayCurrentMusic()
        }

        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
        }
    }

    fun setCurrentPlaylistAndMusic(
        music: Music,
        playlist: ArrayList<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false,
        isForcingNewPlaylist: Boolean = false
    ) {
        // If it's the same music of the same playlist, does nothing
        if (isSameMusic(music.musicId) && isSamePlaylist(isMainPlaylist, playlistId) && !isForcingNewPlaylist) {
            return
        }

        Log.d("VM", isSamePlaylist(isMainPlaylist, playlistId).toString())
        if (!isSamePlaylist(isMainPlaylist, playlistId) || isForcingNewPlaylist) {
            currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
            initialPlaylist = playlist.map { it.copy() } as ArrayList<Music>
            currentPlaylistId = playlistId
            this.isMainPlaylist = isMainPlaylist
            if (shouldServiceBeLaunched) {
                PlayerService.setAndPlayCurrentMusic()
            }
        }

        if (!isSameMusic(music.musicId)) {
            setNewCurrentMusicInformation(music)

            if (shouldServiceBeLaunched) {
                PlayerService.setAndPlayCurrentMusic()
                SharedPrefUtils.setPlayerSavedCurrentMusic()
            }
        }

        if (!shouldServiceBeLaunched) {
            shouldServiceBeLaunched = true
        }
    }

    fun changePlayerMode() {
        if (!isChangingPlayMode) {
            isChangingPlayMode = true
            when (playerMode) {
                PlayerMode.NORMAL -> {
                    // to shuffle mode :
                    currentPlaylist = initialPlaylist.map { it.copy() } as ArrayList<Music>
                    currentPlaylist.shuffle()
                    playerMode = PlayerMode.SHUFFLE
                }
                PlayerMode.SHUFFLE -> {
                    // to loop mode :
                    currentPlaylist =
                        if (currentMusic != null) arrayListOf(currentMusic!!) else ArrayList()
                    playerMode = PlayerMode.LOOP
                }
                PlayerMode.LOOP -> {
                    // to normal mode :
                    currentPlaylist = initialPlaylist.map { it.copy() } as ArrayList<Music>
                    playerMode = PlayerMode.NORMAL
                }
            }
            isChangingPlayMode = false
        }
    }

    fun setMusicFromIndex(index: Int) {
        if (currentPlaylist.lastIndex != -1) {
            currentMusic = if (index <= currentPlaylist.lastIndex) {
                currentPlaylist[max(0, index)]
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