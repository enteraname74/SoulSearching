package com.github.soulsearching.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.*
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
import com.github.soulsearching.classes.*
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.types.PlayerMode
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
    private val settings: SoulSearchingSettings
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
        sortType = _sortType,
        settings = settings
    )

    /**
     * Set information from a given saved list.
     * It will search the saved current music and set it.
     * it will also define the player mode, palette and cover.
     *
     * Information are found from the shared preferences.
     */
    override fun setPlayerInformationFromSavedList(musicList: ArrayList<Music>) {
        currentPlaylist = musicList.map { it.copy() } as ArrayList<Music>
        initialPlaylist = musicList.map { it.copy() } as ArrayList<Music>

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
    override fun addMusicToPlayNext(music: Music) {
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

        settings.saveCurrentMusicInformation(
            currentMusicIndex = getIndexOfCurrentMusic(),
            currentMusicPosition = currentMusicPosition
        )
    }

    /**
     * Remove a music from the playlist if the playlist is the same as a specified one.
     */
    override fun removeMusicIfSamePlaylist(musicId: UUID, playlistId: UUID?) {
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
    override fun removeMusicFromCurrentPlaylist(musicId: UUID) {
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
        settings.saveCurrentMusicInformation(
            currentMusicIndex = getIndexOfCurrentMusic(),
            currentMusicPosition = currentMusicPosition
        )
    }

    /**
     * Change the current music information to the next music.
     */
    override fun setNextMusic() {
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
    override fun setPreviousMusic() {
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
    override fun togglePlayPause() {
        PlayerService.togglePlayPause()
        isPlaying = PlayerService.isPlayerPlaying()
    }

    /**
     * Play a playlist in shuffle and save it.
     */
    override fun playShuffle(playlist: ArrayList<Music>, savePlayerListMethod: KFunction1<ArrayList<UUID>, Unit>) {
        CoroutineScope(Dispatchers.IO).launch {
            currentPlaylistId = null
            isPlaying = false
            isMainPlaylist = false

            playlist.shuffle()

            initialPlaylist = playlist.map { it.copy() } as ArrayList<Music>
            currentPlaylist = playlist.map { it.copy() } as ArrayList<Music>
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
    override fun setCurrentPlaylistAndMusic(
        music: Music,
        playlist: ArrayList<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean,
        isForcingNewPlaylist: Boolean
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
                settings.saveCurrentMusicInformation(
                    currentMusicIndex = getIndexOfCurrentMusic(),
                    currentMusicPosition = currentMusicPosition
                )
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
    override fun changePlayerMode() {
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
    override fun forcePlayerModeToNormal(musicList: ArrayList<Music>) {
        if (!isChangingPlayMode) {
            isChangingPlayMode = true
            playerMode = PlayerMode.NORMAL
            currentPlaylist = musicList.map { it.copy() } as ArrayList<Music>
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
    override fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }
}