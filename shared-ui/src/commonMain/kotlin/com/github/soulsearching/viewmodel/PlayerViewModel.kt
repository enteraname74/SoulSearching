package com.github.soulsearching.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.palette.graphics.Palette
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.types.PlayerMode
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.reflect.KFunction1

/**
 * Represent the PlayerViewModel to used in differents implementations.
 */
abstract class PlayerViewModel {
    protected val _state = MutableStateFlow(MusicState())
    val state = _state.asStateFlow()
    protected val _sortType = MutableStateFlow(SortType.NAME)
    protected val _sortDirection = MutableStateFlow(SortDirection.ASC)
    private var isCounting = false
    protected var isChangingPlayMode = false

    protected var initialPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())

    var currentMusic by mutableStateOf<Music?>(null)
    var currentMusicPosition by mutableIntStateOf(0)
    var currentMusicCover by mutableStateOf<ImageBitmap?>(null)
    var currentColorPalette by mutableStateOf<Palette.Swatch?>(null)

    var currentPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)

    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)

    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)

    var playerMode by mutableStateOf(PlayerMode.NORMAL)

    lateinit var retrieveCoverMethod: (UUID?) -> ImageBitmap?
    lateinit var updateNbPlayed: (UUID) -> Unit

    /**
     * Retrieve the index of the current played music.
     * Return -1 if the current music is null or if it is not found in the current playlist
     */
    fun getIndexOfCurrentMusic(): Int {
        return if (currentMusic == null) {
            -1
        } else {
            currentPlaylist.indexOf(currentPlaylist.find { it.musicId == currentMusic!!.musicId })
        }
    }

    /**
     * Check if a music is the same as the current one.
     * If there is no current music, return false.
     */
    fun isSameMusic(musicId: UUID): Boolean {
        return if (currentMusic == null) {
            false
        } else {
            currentMusic!!.musicId.compareTo(musicId) == 0
        }
    }

    /**
     * Check if a playlist is the same as the current one.
     * We can also check with a isMainPlaylist value (the main playlist, with all songs, does not have
     * a UUID).
     */
    fun isSamePlaylist(isMainPlaylist: Boolean, playlistId: UUID?): Boolean {
        if (playlistId == null && this.currentPlaylistId == null) {
            return isMainPlaylist == this.isMainPlaylist
        } else if (playlistId != null && this.currentPlaylistId != null) {
            return (playlistId.compareTo(currentPlaylistId) == 0) && (isMainPlaylist == this.isMainPlaylist)
        }
        return false
    }

    /**
     * Update information of a song in the initial list, current list and the current music if it's the same.
     */
    fun updateMusic(music: Music) {
        currentMusic?.let {
            if (it.musicId.compareTo(music.musicId) == 0) {
                currentMusic = music
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

    /**
     * Define the current music cover and the current color palette from the cover.
     */
    fun defineCoverAndPaletteFromCoverId(coverId: UUID?) {
        currentMusicCover = retrieveCoverMethod(coverId)
    }

    /**
     * Define the current music.
     */
    protected fun setNewCurrentMusicInformation(music: Music?) {
        currentMusic = music
        currentMusicPosition = 0
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
    }

    /**
     * Retrieve the next music in the current playlist.
     * Return null if nothing is found.
     * Return the first music if we are at the end of the playlist.
     */
    protected fun getNextMusic(currentIndex: Int): Music? {
        return if (currentPlaylist.isNotEmpty()) currentPlaylist[(currentIndex + 1) % currentPlaylist.size] else null
    }

    /**
     * Retrieve the previous music in the current playlist.
     * Return null if nothing is found.
     * Return the last music if we are at the start of the playlist.
     */
    protected fun getPreviousMusic(currentIndex: Int): Music? {
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

    /**
     * Shuffle the current playlist and place the current music at first place in the list.
     */
    protected fun shuffleCurrentList(listToShuffle: java.util.ArrayList<Music>) {
        val tmpList = listToShuffle.map { it.copy() } as java.util.ArrayList<Music>
        tmpList.shuffle()
        currentMusic?.let { music ->
            tmpList.removeIf { it.musicId == music.musicId }
            tmpList.add(0, music)
        }
        currentPlaylist = tmpList
    }

    /**
     * Define the current music from a given index.
     * If the index is out of bound, the current music will be the first one of the playlist.
     */
    fun setMusicFromIndex(index: Int) {
        if (currentPlaylist.isNotEmpty()) {
            currentMusic = if (index <= currentPlaylist.lastIndex) {
                currentPlaylist[Integer.max(0, index)]
            } else {
                currentPlaylist[0]
            }
        }
    }

    /**
     * Reset player view model information.
     */
    fun resetPlayerData() {
        currentMusic = null
        currentMusicCover = null
        currentColorPalette = null
        playerMode = PlayerMode.NORMAL
        initialPlaylist = java.util.ArrayList()
        currentPlaylist = java.util.ArrayList()
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false
        isServiceLaunched = false
        shouldServiceBeLaunched = false
        isCounting = false
    }

    /**
     * Set information from a given saved list.
     * It will search the saved current music and set it.
     * it will also define the player mode, palette and cover.
     *
     * Information are found from the shared preferences.
     */
    abstract fun setPlayerInformationFromSavedList(musicList: ArrayList<Music>)

    /**
     * Add a music to play next.
     * Does nothing if we try to add the current music.
     * If the playlist is empty (nothing is playing), we load the music.
     * It will remove the previous apparition of the music if there was one.
     */
    abstract fun addMusicToPlayNext(music: Music)

    /**
     * Remove a music from the playlist if the playlist is the same as a specified one.
     */
    abstract fun removeMusicIfSamePlaylist(musicId: UUID, playlistId: UUID?)

    /**
     * Remove a music from the current playlist.
     * it no songs are left, the playback will stop.
     */
    abstract fun removeMusicFromCurrentPlaylist(musicId: UUID)

    /**
     * Change the current music information to the next music.
     */
    abstract fun setNextMusic()

    /**
     * Change the current music information to the previous music.
     */
    abstract fun setPreviousMusic()

    /**
     * Play or pause the player.
     */
    abstract fun togglePlayPause()

    /**
     * Play a playlist in shuffle and save it.
     */
    abstract fun playShuffle(playlist: java.util.ArrayList<Music>, savePlayerListMethod: KFunction1<java.util.ArrayList<UUID>, Unit>)

    /**
     * Define the current playlist and music.
     * Primarily used when clicking on a music.
     */
    abstract fun setCurrentPlaylistAndMusic(
        music: Music,
        playlist: java.util.ArrayList<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false,
        isForcingNewPlaylist: Boolean = false
    )

    /**
     * Change the player mode.
     */
    abstract fun changePlayerMode()

    /**
     * Force the player mode the normal.
     * The current playlist will be reset to be like the music list passed in parameter.
     */
    protected abstract fun forcePlayerModeToNormal(musicList: java.util.ArrayList<Music>)

    /**
     * Manage music events.
     */
    abstract fun onMusicEvent(event: MusicEvent)
}
