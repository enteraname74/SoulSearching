package com.github.enteraname74.soulsearching.viewmodel

import android.content.Context
import android.widget.Toast
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicalbum.GetAlbumIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.musicartist.GetArtistIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.soulsearching.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Implementation of a AllMusicsViewModel for Android purposes.
 */
class AllMusicsViewModelAndroidImpl(
    private val context: Context,
    settings: SoulSearchingSettings,
    private val playbackManager: PlaybackManager,
    getAllMusicsSortedUseCase: GetAllMusicsSortedUseCase,
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    getArtistIdFromMusicIdUseCase: GetArtistIdFromMusicIdUseCase,
    getAlbumIdFromMusicIdUseCase: GetAlbumIdFromMusicIdUseCase,
    musicFetcher: MusicFetcher,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    upsertMusicUseCase: UpsertMusicUseCase,
) : AllMusicsViewModel(
    settings = settings,
    musicFetcher = musicFetcher,
    playbackManager = playbackManager,
    getAllMusicsSortedUseCase = getAllMusicsSortedUseCase,
    getAllPlaylistWithMusicsUseCase = getAllPlaylistWithMusicsUseCase,
    getArtistIdFromMusicIdUseCase = getArtistIdFromMusicIdUseCase,
    getAlbumIdFromMusicIdUseCase = getAlbumIdFromMusicIdUseCase,
    deleteMusicUseCase = deleteMusicUseCase,
    toggleMusicFavoriteStatusUseCase = toggleMusicFavoriteStatusUseCase,
    upsertMusicUseCase = upsertMusicUseCase,
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
                    deleteMusicUseCase(music)
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