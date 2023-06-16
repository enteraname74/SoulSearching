package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.database.model.CurrentPlaylistItem
import com.github.soulsearching.database.model.Music
import java.util.UUID

@SuppressLint("MutableCollectionMutableState")
class PlayerViewModel : ViewModel() {
    var currentMusic by mutableStateOf<Music?>(null)
    var playlistInfos by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)
    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)
    var shouldServiceBeLaunched by mutableStateOf(false)

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
        isPlaying = !isPlaying
    }
}