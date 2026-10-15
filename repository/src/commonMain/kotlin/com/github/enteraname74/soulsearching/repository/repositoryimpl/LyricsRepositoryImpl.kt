package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.soulsearching.domain.model.lyrics.LyricsFetchState
import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyricsRequest
import com.github.enteraname74.soulsearching.domain.repository.LyricsRepository
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

class LyricsRepositoryImpl(
    private val lyricsRemoteDataSource: LyricsRemoteDataSource,
    private val lyricsLocalDataSource: LyricsLocalDataSource,
    private val playerLocalDataSource: PlayerLocalDataSource,
    private val settingsRepository: SettingsRepository,
) : LyricsRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeLyrics(
        songProgressionFlow: Flow<Int>,
    ): Flow<LyricsFetchState> =
        settingsRepository.observePlayer().mapLatest { it.isRemoteLyricsFetchEnabled }
            .flatMapLatest { isRemoteFetchEnabled ->
            playerLocalDataSource
                .getCurrentMusic()
                .mapLatest {
                    it?.music?.let { music ->
                        MusicLyricsRequest(
                            localPath = music.localPath,
                            musicName = music.name,
                            artistName = music.album.artist.artistName,
                            albumName = music.album.albumName,
                            remoteFetchAvailable = isRemoteFetchEnabled,
                        )
                    }
                }
                .distinctUntilChanged()
                .flatMapLatest { request ->
                    channelFlow {
                        if (request == null) {
                            send(LyricsFetchState.NoLyricsFound)
                        } else {
                            send(LyricsFetchState.FetchingLyrics)

                            var lyrics = lyricsLocalDataSource.getLyricsOfSong(request)
                            if (lyrics == null && request.remoteFetchAvailable) {
                                lyrics = lyricsRemoteDataSource.getLyricsOfSong(request)
                            }

                            if (lyrics == null) {
                                if (request.remoteFetchAvailable) {
                                    send(LyricsFetchState.NoLyricsFound)
                                } else {
                                    send(LyricsFetchState.NoPermission)
                                }
                            } else {
                                songProgressionFlow.collectLatest { progression ->
                                    send(
                                        LyricsFetchState.FoundLyrics(
                                            lyrics = lyrics,
                                            highlightedLyricsLine = lyrics
                                                .syncedLyrics
                                                ?.indexOfLast { it.timestampMs < progression }
                                                ?.takeIf { it >= 0 }
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
        }
}

