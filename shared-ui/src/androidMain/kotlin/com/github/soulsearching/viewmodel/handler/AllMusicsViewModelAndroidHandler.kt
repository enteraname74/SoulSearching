package com.github.soulsearching.viewmodel.handler

import android.content.Context
import android.widget.Toast
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.R
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllMusicsViewModelHandler
import com.github.soulsearching.domain.model.MusicFetcher
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
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
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings,
    musicFetcher: MusicFetcher,
    private val playbackManager: PlaybackManager
) : AllMusicsViewModelHandler(
    coroutineScope = coroutineScope,
    musicRepository = musicRepository,
    playlistRepository = playlistRepository,
    musicPlaylistRepository = musicPlaylistRepository,
    albumRepository = albumRepository,
    artistRepository = artistRepository,
    musicAlbumRepository = musicAlbumRepository,
    musicArtistRepository = musicArtistRepository,
    albumArtistRepository = albumArtistRepository,
    imageCoverRepository = imageCoverRepository,
    settings = settings,
    musicFetcher = musicFetcher,
    playbackManager = playbackManager
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