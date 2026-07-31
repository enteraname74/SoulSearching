package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.domain.usecase.playlist.UploadPlaylistToCloudUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertCloudPlaylistUseCase
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.uuid.Uuid

/**
 * Sync local songs with remote one.
 * The steps are :
 * - sending recently updated songs to remote, or never sent ones
 * - fetching recently updated songs to remote and saving them
 */
class SyncDataWithCloudUseCase(
    private val musicRepository: MusicRepository,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    private val cloudMusicToMusicUseCase: CloudMusicToMusicUseCase,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
    private val updateMusicToCloudUseCase: UpdateMusicToCloudUseCase,
    private val uploadMusicToCloudUseCase: UploadMusicToCloudUseCase,
    private val uploadPlaylistToCloudUseCase: UploadPlaylistToCloudUseCase,
    private val upsertCloudPlaylistUseCase: UpsertCloudPlaylistUseCase,
    private val playlistRepository: PlaylistRepository,
) {
    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    suspend operator fun invoke(): SoulResult<Unit> {
        val result: SoulResult<Unit> = SoulResult.runCatching {
            _state.value = State.ClearingRemoteMusicIds
            val idsNoLongerOnCloud: List<String> = musicRepository.getDeletedRemoteMusicIds()
            musicRepository.deleteAllRemoteFieldsOfIds(idsNoLongerOnCloud)

            _state.value = State.CheckingMusicsToSend
            val musicsToSend: List<Music> = musicRepository.getAllToSendToCloud()
            if (musicsToSend.isEmpty()) {
                _state.value = State.NoMusicsToSend
            }

            // TODO SYNC: Let user choose its merge mode.
            val mergeMode = MergeMode.RemoteFirst

            /*
            We need to differentiate the songs to update (already on the cloud) from the one to sent to
            upload (not on the cloud).
             */
            val (musicsToUpdate, musicsToUpload) = musicsToSend.partition { music ->
                music.remoteId != null
            }

            val cachedAlbums: MutableSet<Album> = mutableSetOf()
            val cachedArtists: MutableSet<Artist> = mutableSetOf()
            val cachedMusics: MutableSet<Music> = mutableSetOf()

            // We keep track of the updated/uploaded songs to avoid re-saving them after the next sync.
            val builtMusics: List<Music> = musicsToUpdate.mapIndexedNotNull { index, music ->
                _state.value = State.UpdateMusics(
                    progress = buildProgress(
                        index = index,
                        size = musicsToUpdate.size,
                    )
                )
                val music = updateMusicToCloudUseCase(
                    music = music,
                    mergeMode = mergeMode,
                    cachedArtists = cachedArtists,
                    cachedAlbums = cachedAlbums,
                    cachedMusics = cachedMusics,
                )
                music?.let {
                    cachedAlbums += music.album
                    cachedArtists += music.artists
                    cachedArtists += music.album.artist
                    cachedMusics += music
                }
                music

            } + musicsToUpload.mapIndexedNotNull { index, music ->
                _state.value = State.UploadMusics(
                    progress = buildProgress(
                        index = index,
                        size = musicsToUpload.size,
                    )
                )
                val music = uploadMusicToCloudUseCase(
                    music = music,
                    mergeMode = mergeMode,
                    cachedArtists = cachedArtists,
                    cachedAlbums = cachedAlbums,
                    cachedMusics = cachedMusics,
                )
                music?.let {
                    cachedAlbums += music.album
                    cachedArtists += music.artists
                    cachedArtists += music.album.artist
                    cachedMusics += music
                }
                music
            }
            musicRepository.upsertAll(builtMusics)
            val savedRemoteIds = builtMusics.map { it.remoteId }

            _state.value = State.FetchingFromRemote
            val lastSyncMillis = cloudPreferencesRepository.getLastSyncMillis()
            // Fetching updated songs from cloud
            val updatedRemoteSongs: List<CloudMusic> = musicRepository.fetchUpdatedSongsFromCloud(
                lastSyncMillis = lastSyncMillis,
            )

            // Keeping only songs not already handled
            val filteredSongsToSave = updatedRemoteSongs.filter { music ->
                savedRemoteIds.none { it == music.fingerprint }
            }
            // Saving each song, with their album and artist
            val toSave = filteredSongsToSave.mapIndexed { index, music ->
                _state.value = State.SavingRemote(
                    progress = buildProgress(
                        index = index,
                        size = filteredSongsToSave.size,
                    )
                )
                val music = cloudMusicToMusicUseCase(
                    cloudMusic = music,
                    mergeMode = mergeMode,
                    cachedArtists = cachedArtists,
                    cachedAlbums = cachedAlbums,
                    cachedMusics = cachedMusics,
                )
                cachedAlbums += music.album
                cachedArtists += music.artists
                cachedArtists += music.album.artist
                cachedMusics += music

                music
            }
            musicRepository.upsertAll(toSave)
            _state.value = State.Cleaning

            // Deleting potential empty albums, artists and music (localPath and remoteId null).
            musicRepository.deleteNotExisting()
            deleteEmptyAlbumsAndArtistsUseCase()

            /*
            Choice has been made to consider playlists to always be remote first,
            to ensure maximum sync between them.
             */
            handlePlaylists(
                lastSyncMillis = lastSyncMillis,
                mergeMode = MergeMode.RemoteFirst,
            )

            /*
            playlists updated on backend can have a more recent updatedAt field than what we can have here,
            so will we check also against it.
             */
            val latestFromPlaylists = playlistRepository.getLatestUpdatedAt() ?: 0L
            cloudPreferencesRepository.setLastSyncMillis(max(DateUtils.now(), latestFromPlaylists))
        }

        _state.value = if (result.isError()) State.Failure else State.Finish

        return result
    }

    private suspend fun handlePlaylists(
        lastSyncMillis: Long?,
        mergeMode: MergeMode,
    ) {
        // For playlists, if synced playlists are no longer on the cloud, we will delete them
        _state.value = State.ClearingRemotePlaylistIds
        val idsNoLongerOnCloud: List<Uuid> = playlistRepository.getDeletedRemotePlaylistIds()
        playlistRepository.deleteAllFromRemote(remoteIds = idsNoLongerOnCloud)

        /*
        We retrieve playlists from the cloud.
        If there is already an already existing playlist, we will merge them.
        No songs should be deleted from playlists.
         */
        _state.value = State.FetchingRemotePlaylists
        val updatedFromCloud: List<CloudPlaylist> = playlistRepository.fetchUpdatedPlaylistsFromCloud(
            lastSyncMillis = lastSyncMillis,
        )
        _state.value = State.SavingRemotePlaylists
        upsertCloudPlaylistUseCase(
            cloudPlaylists = updatedFromCloud,
            mergeMode = mergeMode,
        )

        /*
        We then retrieve all playlists to send to cloud (old timestamp, no remote ids)
         */
        val playlistsToSend: List<PlaylistWithMusics> = playlistRepository.getAllToSendToCloud()
        playlistsToSend.forEachIndexed { index, playlist ->
            _state.value = State.UploadingPlaylists(
                progress = buildProgress(
                    index = index,
                    size = playlistsToSend.size,
                )
            )
            uploadPlaylistToCloudUseCase(
                playlistWithMusics = playlist,
                mergeMode = mergeMode,
            )
        }
        playlistRepository.deleteAllEmptyExceptFavorite()
    }

    private fun buildProgress(
        index: Int,
        size: Int,
    ): Float =
        if (size <= 1) {
            1f
        } else {
            index.toFloat() / (size - 1)
        }

    sealed interface State {

        sealed interface WorkingState : State
        sealed interface ProgressState : WorkingState {
            val progress: Float
        }

        sealed interface EndState : State

        data object Idle : EndState
        data object ClearingRemoteMusicIds : WorkingState
        data object CheckingMusicsToSend : WorkingState

        data object NoMusicsToSend : EndState

        data class UpdateMusics(override val progress: Float) : ProgressState

        data class UploadMusics(override val progress: Float) : ProgressState

        data object FetchingFromRemote : WorkingState

        data class SavingRemote(override val progress: Float) : ProgressState

        data object Cleaning : WorkingState

        data object FetchingRemotePlaylists : WorkingState

        data object SavingRemotePlaylists : WorkingState

        data object ClearingRemotePlaylistIds : WorkingState

        data class UploadingPlaylists(override val progress: Float) : ProgressState

        data object Failure : EndState

        data object Finish : EndState
    }
}
