package com.github.soulsearching.viewmodel.handler

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.handlers.MusicEventHandler
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.types.PlayerMode
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.reflect.KFunction1

/**
 * Handler for managing the PlayerViewModel.
 */
class PlayerViewModelHandler(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    private val settings: SoulSearchingSettings,
    private val playbackManager: PlaybackManager,
    private val colorThemeManager: ColorThemeManager
): ViewModelHandler {
    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)
    private var isChangingPlayMode = false

    private var initialPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())

    var currentMusic by mutableStateOf<Music?>(null)
    var currentMusicPosition by mutableIntStateOf(0)
    var currentMusicCover by mutableStateOf<ImageBitmap?>(null)

    var currentPlaylist by mutableStateOf<ArrayList<Music>>(ArrayList())
    var currentPlaylistId by mutableStateOf<UUID?>(null)

    var isMainPlaylist by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)

    var shouldServiceBeLaunched by mutableStateOf(false)
    var isServiceLaunched by mutableStateOf(false)

    var playerMode by mutableStateOf(PlayerMode.NORMAL)

    lateinit var retrieveCoverMethod: (UUID?) -> ImageBitmap?
    lateinit var updateNbPlayed: (UUID) -> Unit

    private val musicEventHandler = MusicEventHandler(
        privateState = _musicState,
        publicState = musicState,
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
        sortType = _sortType,
        settings = settings,
        playbackManager = playbackManager
    )

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
    private fun setNewCurrentMusicInformation(music: Music?) {
        currentMusic = music
        currentMusicPosition = 0
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
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
     * Shuffle the current playlist and place the current music at first place in the list.
     */
    private fun shuffleCurrentList(listToShuffle: java.util.ArrayList<Music>) {
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
    private fun setMusicFromIndex(index: Int) {
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
        colorThemeManager.currentColorPalette = null
        playerMode = PlayerMode.NORMAL
        initialPlaylist = java.util.ArrayList()
        currentPlaylist = java.util.ArrayList()
        currentPlaylistId = null
        isPlaying = false
        isMainPlaylist = false
        isServiceLaunched = false
        shouldServiceBeLaunched = false
    }

    /**
     * Set information from a given saved list.
     * It will search the saved current music and set it.
     * it will also define the player mode, palette and cover.
     *
     * Information are found from the shared preferences.
     */
    fun setPlayerInformationFromSavedList(musicList: java.util.ArrayList<Music>) {
        currentPlaylist = musicList.map { it.copy() } as java.util.ArrayList<Music>
        initialPlaylist = musicList.map { it.copy() } as java.util.ArrayList<Music>

        val index = settings.getInt(SoulSearchingSettings.PLAYER_MUSIC_INDEX_KEY, -1)
        val position = settings.getInt(SoulSearchingSettings.PLAYER_MUSIC_POSITION_KEY, 0)

        setMusicFromIndex(index)
        currentMusicPosition = position

        playerMode = settings.getPlayerMode()
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
    }

    /**
     * Add a music to play next.
     * Does nothing if we try to add the current music.
     * If the playlist is empty (nothing is playing), we load the music.
     * It will remove the previous apparition of the music if there was one.
     */
    fun addMusicToPlayNext(music: Music) {
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
            playbackManager.initializeMusicPlayerManager(
                isFromSavedList = true
            )
        }

        // We make sure to remove the music if it's already in the playlist :
        currentPlaylist.removeIf { it.musicId == music.musicId }

        // Finally, we add the new next music :
        val currentIndex = getIndexOfCurrentMusic()
        currentPlaylist.add(currentIndex + 1, music)

        settings.saveCurrentMusicInformation(
            currentMusicIndex = getIndexOfCurrentMusic(),
            currentMusicPosition = currentMusicPosition
        )
    }

    /**
     * Remove a music from the playlist if the playlist is the same as a specified one.
     */
    fun removeMusicIfSamePlaylist(musicId: UUID, playlistId: UUID?) {
        if (playlistId == null && currentPlaylistId == null) {
            removeMusicFromCurrentPlaylist(musicId)
        } else if (playlistId != null && currentPlaylistId != null) {
            if (playlistId.compareTo(currentPlaylistId) == 0) {
                removeMusicFromCurrentPlaylist(musicId)
            }
        }
    }

    /**
     * Remove a music from the current playlist.
     * it no songs are left, the playback will stop.
     */
    fun removeMusicFromCurrentPlaylist(musicId: UUID) {
        val currentIndex = getIndexOfCurrentMusic()
        currentPlaylist.removeIf { it.musicId == musicId }

        // If no songs is left in the queue, stop playing :
        if (currentPlaylist.isEmpty()) {
            playbackManager.stopPlayback()
        } else {
            // If same music than the one played, play next song :
            currentMusic?.let {
                if (it.musicId.compareTo(musicId) == 0) {
                    // We place ourself in the previous music :
                    currentMusic = currentPlaylist[(currentIndex) % currentPlaylist.size]

                    playbackManager.setAndPlayCurrentMusic()
                }
            }
        }
        settings.saveCurrentMusicInformation(
            currentMusicIndex = getIndexOfCurrentMusic(),
            currentMusicPosition = currentMusicPosition
        )
    }

    /**
     * Change the current music information to the next music.
     */
    fun setNextMusic() {
        if (currentPlaylist.size != 0) {
            val currentIndex = getIndexOfCurrentMusic()

            setNewCurrentMusicInformation(getNextMusic(currentIndex))

            settings.saveCurrentMusicInformation(
                currentMusicIndex = getIndexOfCurrentMusic(),
                currentMusicPosition = currentMusicPosition
            )
        }
    }

    /**
     * Change the current music information to the previous music.
     */
    fun setPreviousMusic() {
        val currentIndex = getIndexOfCurrentMusic()

        setNewCurrentMusicInformation(getPreviousMusic(currentIndex))

        settings.saveCurrentMusicInformation(
            currentMusicIndex = getIndexOfCurrentMusic(),
            currentMusicPosition = currentMusicPosition
        )
    }

    /**
     * Play or pause the player.
     */
    fun togglePlayPause() {
        playbackManager.togglePlayPause()
        isPlaying = playbackManager.isPlayerPlaying()
    }

    /**
     * Play a playlist in shuffle and save it.
     */
    fun playShuffle(playlist: java.util.ArrayList<Music>, savePlayerListMethod: KFunction1<ArrayList<UUID>, Unit>) {
        CoroutineScope(Dispatchers.IO).launch {
            currentPlaylistId = null
            isPlaying = false
            isMainPlaylist = false

            playlist.shuffle()

            initialPlaylist = playlist.map { it.copy() } as java.util.ArrayList<Music>
            currentPlaylist = playlist.map { it.copy() } as java.util.ArrayList<Music>
            playerMode = PlayerMode.NORMAL
            settings.setPlayerMode(
                key = SoulSearchingSettings.PLAYER_MODE_KEY,
                value = playerMode
            )
            settings.saveCurrentMusicInformation(
                currentMusicIndex = getIndexOfCurrentMusic(),
                currentMusicPosition = currentMusicPosition
            )

            setNewCurrentMusicInformation(currentPlaylist[0])
            savePlayerListMethod(currentPlaylist.map { it.musicId } as java.util.ArrayList<UUID>)

            if (shouldServiceBeLaunched) {
                playbackManager.setAndPlayCurrentMusic()
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
        playlist: java.util.ArrayList<Music>,
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
                currentPlaylist = playlist.map { it.copy() } as java.util.ArrayList<Music>
                initialPlaylist = playlist.map { it.copy() } as java.util.ArrayList<Music>
                currentPlaylistId = playlistId
                this@PlayerViewModelHandler.isMainPlaylist = isMainPlaylist
                settings.saveCurrentMusicInformation(
                    currentMusicIndex = getIndexOfCurrentMusic(),
                    currentMusicPosition = currentMusicPosition
                )
            }

            if (notSameMusic) {
                setNewCurrentMusicInformation(music)
            }

            if (shouldServiceBeLaunched) {
                playbackManager.setAndPlayCurrentMusic()
            } else {
                shouldServiceBeLaunched = true
            }
        }
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
                        if (currentMusic != null) arrayListOf(currentMusic!!) else java.util.ArrayList()
                    currentPlaylistId = null
                    playerMode = PlayerMode.LOOP
                }
                PlayerMode.LOOP -> {
                    // to normal mode :
                    currentPlaylist = initialPlaylist.map { it.copy() } as java.util.ArrayList<Music>
                    currentPlaylistId = null
                    playerMode = PlayerMode.NORMAL
                }
            }
            settings.setPlayerMode(
                key = SoulSearchingSettings.PLAYER_MODE_KEY,
                value = playerMode
            )
            settings.saveCurrentMusicInformation(
                currentMusicIndex = getIndexOfCurrentMusic(),
                currentMusicPosition = currentMusicPosition
            )
            isChangingPlayMode = false
        }
    }

    /**
     * Force the player mode the normal.
     * The current playlist will be reset to be like the music list passed in parameter.
     */
    private fun forcePlayerModeToNormal(musicList: java.util.ArrayList<Music>) {
        if (!isChangingPlayMode) {
            isChangingPlayMode = true
            playerMode = PlayerMode.NORMAL
            currentPlaylist = musicList.map { it.copy() } as java.util.ArrayList<Music>
            settings.saveCurrentMusicInformation(
                currentMusicIndex = getIndexOfCurrentMusic(),
                currentMusicPosition = currentMusicPosition
            )
            isChangingPlayMode = false
        }
    }

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }
}