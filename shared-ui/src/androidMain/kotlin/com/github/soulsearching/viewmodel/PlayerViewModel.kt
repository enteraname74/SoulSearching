package com.github.soulsearching.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.github.enteraname74.model.Music
import com.github.enteraname74.repository.AlbumArtistRepository
import com.github.enteraname74.repository.AlbumRepository
import com.github.enteraname74.repository.ArtistRepository
import com.github.enteraname74.repository.ImageCoverRepository
import com.github.enteraname74.repository.MusicAlbumRepository
import com.github.enteraname74.repository.MusicArtistRepository
import com.github.enteraname74.repository.MusicPlaylistRepository
import com.github.enteraname74.repository.MusicRepository
import com.github.enteraname74.repository.PlaylistRepository
import com.github.soulsearching.classes.*
import com.github.soulsearching.classes.types.PlayerMode
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.classes.utils.ColorPaletteUtils
import com.github.soulsearching.classes.utils.SharedPrefUtils
import com.github.soulsearching.classes.utils.Utils
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.states.MusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Integer.max
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KFunction1

/**
 * View model for the player.
 */
@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class PlayerViewModel @Inject constructor(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(MusicState())
    val state = _state.asStateFlow()
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)

    private val musicEventHandler = MusicEventHandler(
        privateState = _state,
        publicState = state,
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        albumArtistRepository = albumArtistRepository,
        imageCoverRepository = imageCoverRepository,
        sortDirection = _sortDirection,
        sortType = _sortType
    )

    var currentMusic by mutableStateOf<Music?>(null)
    var currentMusicPosition by mutableIntStateOf(0)
    var currentMusicCover by mutableStateOf<ImageBitmap?>(null)
    var currentColorPalette by mutableStateOf<Palette.Swatch?>(null)

    private var initialPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)

    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)

    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)

    var playerMode by mutableStateOf(PlayerMode.NORMAL)

    lateinit var retrieveCoverMethod: (UUID?) -> ImageBitmap?
    lateinit var updateNbPlayed: (UUID) -> Unit

    private var isCounting = false
    private var isChangingPlayMode = false

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
     * Set information from a given saved list.
     * It will search the saved current music and set it.
     * it will also define the player mode, palette and cover.
     *
     * Information are found from the shared preferences.
     */
    fun setPlayerInformationFromSavedList(musicList: ArrayList<Music>) {
        currentPlaylist = musicList.map { it.copy() } as ArrayList<Music>
        initialPlaylist = musicList.map { it.copy() } as ArrayList<Music>

        SharedPrefUtils.getPlayerSavedCurrentMusic()
        SharedPrefUtils.getPlayerMode()
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
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
        currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)
    }

    /**
     * Define the current music.
     */
    private fun setNewCurrentMusicInformation(music: Music?) {
        currentMusic = music
        currentMusicPosition = 0
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
    }

    /**
     * Add a music to play next.
     * Does nothing if we try to add the current music.
     * If the playlist is empty (nothing is playing), we load the music.
     * It will remove the previous apparition of the music if there was one.
     */
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

    /**
     * Remove a music from the playlist if the playlist is the same as a specified one.
     */
    fun removeMusicIfSamePlaylist(musicId: UUID, context: Context, playlistId: UUID?) {
        if (playlistId == null && currentPlaylistId == null) {
            removeMusicFromCurrentPlaylist(musicId, context)
        } else if (playlistId != null && currentPlaylistId != null) {
            if (playlistId.compareTo(currentPlaylistId) == 0) {
                removeMusicFromCurrentPlaylist(musicId, context)
            }
        }
    }

    /**
     * Remove a music from the current playlist.
     * it no songs are left, the playback will stop.
     */
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

    /**
     * Change the current music information to the next music.
     */
    fun setNextMusic() {
        if (currentPlaylist.size != 0) {
            val currentIndex = getIndexOfCurrentMusic()

            setNewCurrentMusicInformation(getNextMusic(currentIndex))

            SharedPrefUtils.setPlayerSavedCurrentMusic()
        }
    }

    /**
     * Retrieve the next music in the current playlist.
     * Return null if nothing is found.
     * Return the first music if we are at the end of the playlist.
     */
    private fun getNextMusic(currentIndex: Int): Music? {
        return if (currentPlaylist.isNotEmpty()) currentPlaylist[(currentIndex + 1) % currentPlaylist.size] else null
    }

    /**
     * Retrieve the previous music in the current playlist.
     * Return null if nothing is found.
     * Return the last music if we are at the start of the playlist.
     */
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

    /**
     * Change the current music information to the previous music.
     */
    fun setPreviousMusic() {
        val currentIndex = getIndexOfCurrentMusic()

        setNewCurrentMusicInformation(getPreviousMusic(currentIndex))

        SharedPrefUtils.setPlayerSavedCurrentMusic()
    }

    /**
     * Play or pause the player.
     */
    fun togglePlayPause() {
        PlayerService.togglePlayPause()
        isPlaying = PlayerService.isPlayerPlaying()
    }

    /**
     * Play a playlist in shuffle and save it.
     */
    fun playShuffle(playlist: ArrayList<Music>, savePlayerListMethod: KFunction1<ArrayList<UUID>, Unit>) {
        CoroutineScope(Dispatchers.IO).launch {
            currentPlaylistId = null
            isPlaying = false
            isMainPlaylist = false

            playlist.shuffle()

            initialPlaylist = playlist.map { it.copy() } as ArrayList<Music>
            currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
            playerMode = PlayerMode.NORMAL
            SharedPrefUtils.setPlayerMode()
            SharedPrefUtils.setPlayerSavedCurrentMusic()

            setNewCurrentMusicInformation(currentPlaylist[0])
            savePlayerListMethod(currentPlaylist.map { it.musicId } as ArrayList<UUID>)

            if (shouldServiceBeLaunched) {
                PlayerService.setAndPlayCurrentMusic()
            }

            if (!shouldServiceBeLaunched) {
                shouldServiceBeLaunched = true
            }
        }
    }

    /**
     * Define the current playlist and music.
     * Primarily used when clicking on a music.
     */
    fun setCurrentPlaylistAndMusic(
        music: Music,
        playlist: ArrayList<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false,
        isForcingNewPlaylist: Boolean = false
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // When selecting a music manually, we force the player mode to normal:
            forcePlayerModeToNormal(playlist)

            // If it's the same music of the same playlist, does nothing
            if (isSameMusic(music.musicId) && isSamePlaylist(
                    isMainPlaylist,
                    playlistId
                ) && !isForcingNewPlaylist
            ) {
                return@launch
            }

            val shouldForcePlaylistOrNewPlaylist = !isSamePlaylist(isMainPlaylist, playlistId) || isForcingNewPlaylist
            val notSameMusic = !isSameMusic(music.musicId)

            if (shouldForcePlaylistOrNewPlaylist) {
                currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
                initialPlaylist = playlist.map { it.copy() } as ArrayList<Music>
                currentPlaylistId = playlistId
                this@PlayerViewModel.isMainPlaylist = isMainPlaylist
                SharedPrefUtils.setPlayerSavedCurrentMusic()
            }

            if (notSameMusic) {
                setNewCurrentMusicInformation(music)
            }

            if (shouldServiceBeLaunched) {
                PlayerService.setAndPlayCurrentMusic()
            } else {
                shouldServiceBeLaunched = true
            }
        }
    }

    /**
     * Shuffle the current playlist and place the current music at first place in the list.
     */
    private fun shuffleCurrentList(listToShuffle: ArrayList<Music>) {
        val tmpList = listToShuffle.map { it.copy() } as ArrayList<Music>
        tmpList.shuffle()
        currentMusic?.let { music ->
            tmpList.removeIf { it.musicId == music.musicId }
            tmpList.add(0, music)
        }
        currentPlaylist = tmpList
    }

    /**
     * Change the player mode.
     */
    fun changePlayerMode() {
        if (!isChangingPlayMode) {
            isChangingPlayMode = true
            when (playerMode) {
                PlayerMode.NORMAL -> {
                    // to shuffle mode :
                    shuffleCurrentList(currentPlaylist)
                    playerMode = PlayerMode.SHUFFLE
                }
                PlayerMode.SHUFFLE -> {
                    // to loop mode :
                    currentPlaylist =
                        if (currentMusic != null) arrayListOf(currentMusic!!) else ArrayList()
                    currentPlaylistId = null
                    playerMode = PlayerMode.LOOP
                }
                PlayerMode.LOOP -> {
                    // to normal mode :
                    currentPlaylist = initialPlaylist.map { it.copy() } as ArrayList<Music>
                    currentPlaylistId = null
                    playerMode = PlayerMode.NORMAL
                }
            }
            SharedPrefUtils.setPlayerMode()
            SharedPrefUtils.setPlayerSavedCurrentMusic()
            isChangingPlayMode = false
        }
    }

    /**
     * Force the player mode the normal.
     * The current playlist will be reset to be like the music list passed in parameter.
     */
    private fun forcePlayerModeToNormal(musicList: ArrayList<Music>) {
        if (!isChangingPlayMode) {
            isChangingPlayMode = true
            playerMode = PlayerMode.NORMAL
            currentPlaylist = musicList.map { it.copy() } as ArrayList<Music>
            SharedPrefUtils.setPlayerSavedCurrentMusic()
            isChangingPlayMode = false
        }
    }

    /**
     * Define the current music from a given index.
     * If the index is out of bound, the current music will be the first one of the playlist.
     */
    fun setMusicFromIndex(index: Int) {
        if (currentPlaylist.isNotEmpty()) {
            currentMusic = if (index <= currentPlaylist.lastIndex) {
                currentPlaylist[max(0, index)]
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
        initialPlaylist = ArrayList()
        currentPlaylist = ArrayList()
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false
        isServiceLaunched = false
        shouldServiceBeLaunched = false
        isCounting = false
    }

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }
}