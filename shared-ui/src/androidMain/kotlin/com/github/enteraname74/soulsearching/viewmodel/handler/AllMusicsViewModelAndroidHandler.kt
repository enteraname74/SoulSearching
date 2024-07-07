package com.github.enteraname74.soulsearching.viewmodel.handler

import android.content.Context
import android.widget.Toast
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.R
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.domain.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.AllMusicsViewModelHandler
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Implementation of a AllMusicsViewModelHandler for Android purposes.
 */
class AllMusicsViewModelAndroidHandler(
    private val context: Context,
    coroutineScope: CoroutineScope,
    private val musicRepository: MusicRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    playlistRepository: PlaylistRepository,
    settings: SoulSearchingSettings,
    musicFetcher: MusicFetcher,
    private val playbackManager: PlaybackManager,
) : AllMusicsViewModelHandler(
    coroutineScope = coroutineScope,
    musicRepository = musicRepository,
    musicAlbumRepository = musicAlbumRepository,
    musicArtistRepository = musicArtistRepository,
    settings = settings,
    musicFetcher = musicFetcher,
    playbackManager = playbackManager,
    playlistRepository = playlistRepository
) {
    /**
     * Check all musics and delete the one that does not exists (if the path of the music is not valid).
     */
    override fun checkAndDeleteMusicIfNotExist() {
        CoroutineScope(Dispatchers.IO).launch {
            var deleteCount = 0
            for (music in state.value.musics) {
                if (!File(music.path).exists()) {
                    playbackManager.removeSongFromPlayedPlaylist(
                        music.musicId
                    )
                    musicRepository.deleteMusic(music)
                    deleteCount += 1
                }
            }

            if (deleteCount == 1) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.deleted_music),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (deleteCount > 1) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.deleted_musics, deleteCount),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}