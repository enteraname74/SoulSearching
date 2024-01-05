package com.github.soulsearching.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.github.soulsearching.classes.*
import com.github.soulsearching.classes.enumsAndTypes.PlayerMode
import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Music
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
import kotlin.collections.ArrayList
import kotlin.reflect.KFunction1

@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val playlistDao: PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val imageCoverDao: ImageCoverDao,
) : ViewModel() {
    private val _state = MutableStateFlow(MusicState())
    val state = _state.asStateFlow()
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)

    var currentMusic by mutableStateOf<Music?>(null)
    var currentMusicPosition by mutableIntStateOf(0)
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

    fun setPlayerInformationFromSavedList(musicList: ArrayList<Music>) {
        currentPlaylist = musicList.map { it.copy() } as ArrayList<Music>
        initialPlaylist = musicList.map { it.copy() } as ArrayList<Music>

        SharedPrefUtils.getPlayerSavedCurrentMusic()
        SharedPrefUtils.getPlayerMode()
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
    }

    fun isSameMusic(musicId: UUID): Boolean {
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

    fun defineCoverAndPaletteFromCoverId(coverId: UUID?) {
        currentMusicCover = retrieveCoverMethod(coverId)
        currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(currentMusicCover)
    }

    private fun setNewCurrentMusicInformation(music: Music?) {
        currentMusic = music
        currentMusicPosition = 0
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
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

    private fun shuffleCurrentList(listToShuffle: ArrayList<Music>) {
        val tmpList = listToShuffle.map { it.copy() } as ArrayList<Music>
        tmpList.shuffle()
        currentMusic?.let { music ->
            tmpList.removeIf { it.musicId == music.musicId }
            tmpList.add(0, music)
        }
        currentPlaylist = tmpList
    }

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

    private fun forcePlayerModeToNormal(musicList: ArrayList<Music>) {
        if (!isChangingPlayMode) {
            isChangingPlayMode = true
            playerMode = PlayerMode.NORMAL
            currentPlaylist = musicList.map { it.copy() } as ArrayList<Music>
            SharedPrefUtils.setPlayerSavedCurrentMusic()
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

    fun onMusicEvent(event: MusicEvent) {
        EventUtils.onMusicEvent(
            event = event,
            _state = _state,
            state = state,
            musicDao = musicDao,
            playlistDao = playlistDao,
            albumDao = albumDao,
            artistDao = artistDao,
            musicPlaylistDao = musicPlaylistDao,
            musicAlbumDao = musicAlbumDao,
            musicArtistDao = musicArtistDao,
            albumArtistDao = albumArtistDao,
            _sortDirection = _sortDirection,
            _sortType = _sortType,
            imageCoverDao = imageCoverDao
        )
    }
}