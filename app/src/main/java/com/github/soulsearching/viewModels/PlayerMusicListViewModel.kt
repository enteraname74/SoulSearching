package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.database.dao.PlayerMusicDao
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.PlayerMusic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerMusicListViewModel @Inject constructor(
    private val playerMusicDao: PlayerMusicDao
) : ViewModel() {
    private var isDoingOperations : Boolean = false

    suspend fun getPlayerMusicList(): ArrayList<Music> {
        val playerWithMusics = playerMusicDao.getAllPlayerMusics()

        return playerWithMusics.filter { it.music != null }.map { it.music!! } as ArrayList<Music>
    }

    fun savePlayerMusicList(musicList : ArrayList<Music>) {
        if (!isDoingOperations) {
            CoroutineScope(Dispatchers.IO).launch {
                isDoingOperations = true
                for (music in musicList) {
                    playerMusicDao.insertPlayerMusic(
                        PlayerMusic = PlayerMusic(
                            playerMusicId = music.musicId
                        )
                    )
                }
                SharedPrefUtils.setPlayerSavedCurrentMusic()
                isDoingOperations = false
            }
        }
    }

    fun resetPlayerMusicList() {
        CoroutineScope(Dispatchers.IO).launch {
            playerMusicDao.deleteAllPlayerMusic()
        }
        SharedPrefUtils.setPlayerSavedCurrentMusic()
    }
}