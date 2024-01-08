package com.github.soulsearching.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.*
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
import com.github.soulsearching.classes.utils.SharedPrefUtils
import com.github.soulsearching.classes.utils.AndroidUtils
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.*
import java.util.*
import kotlin.reflect.KFunction1

/**
 * View model for the player.
 */
@SuppressLint("MutableCollectionMutableState")
class PlayerViewModelImpl(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
): PlayerViewModel() {
    lateinit var context: Context
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
            AndroidUtils.launchService(
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
                this@PlayerViewModelImpl.isMainPlaylist = isMainPlaylist
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
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }
}