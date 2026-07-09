package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.CloudPlaylist
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
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
import kotlinx.coroutines.flow.first

/**
 * Sync local songs with remote one.
 * The steps are :
 * - sending recently updated songs to remote, or never sent ones
 * - fetching recently updated songs to remote and saving them
 */
class SyncDataWithCloudUseCase(
    private val musicRepository: MusicRepository,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    private val upsertCloudMusicUseCase: UpsertCloudMusicUseCase,
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
        println("CLUELESS -- after before sync: ${musicRepository.getAll().first().size}")
        val result: SoulResult<Unit> = SoulResult.runCatching {
            _state.value = State.ClearingRemoteIds
            val idsNoLongerOnCloud: List<String> = musicRepository.getDeletedRemoteMusicIds()
            musicRepository.clearRemoteIds(idsNoLongerOnCloud)

            _state.value = State.CheckingMusicsToSend
            val musicsToSend: List<Music> = musicRepository.getAllToSendToCloud()
            if (musicsToSend.isEmpty()) {
                _state.value = State.NoMusicsToSend
            }

            val a = musicRepository.getAllFromQuickAccess().first().map { it.lastUpdatedMillis }
            val pref = cloudPreferencesRepository.getLastSyncMillis()
            val isAfter = pref?.let { a.firstOrNull()?.let { it > pref } }

            println("CLUELESS -- musics to send: $musicsToSend, ${musicRepository.getAll().first().size}, $a, $pref, $isAfter")

            // TODO SYNC: Let user choose its merge mode.
            val mergeMode = MergeMode.LocalFirst

            /*
            We need to differentiate the songs to update (already on the cloud) from the one to sent to
            upload (not on the cloud).
             */
            val (musicsToUpdate, musicsToUpload) = musicsToSend.partition { music ->
                music.remoteId != null
            }

            // We keep track of the updated/uploaded songs to avoid re-saving them after the next sync.
            val savedRemoteIds: List<String> = musicsToUpdate.mapIndexedNotNull { index, music ->
                _state.value = State.UpdateMusics(
                    progress = buildProgress(
                        index = index,
                        size = musicsToUpdate.size,
                    )
                )
                updateMusicToCloudUseCase(
                    music = music,
                    mergeMode = mergeMode,
                )?.remoteId
            } + musicsToUpload.mapIndexedNotNull { index, music ->
                _state.value = State.UploadMusics(
                    progress = buildProgress(
                        index = index,
                        size = musicsToUpload.size,
                    )
                )
                uploadMusicToCloudUseCase(
                    music = music,
                    mergeMode = mergeMode,
                )?.remoteId
            }

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
            filteredSongsToSave.forEachIndexed { index, music ->
                _state.value = State.SavingRemote(
                    progress = buildProgress(
                        index = index,
                        size = filteredSongsToSave.size,
                    )
                )
                upsertCloudMusicUseCase(
                    cloudMusic = music,
                    mergeMode = mergeMode,
                )
            }
            _state.value = State.Cleaning
            // Deleting potential empty albums, artists and music (localPath and remoteId null).
            println("CLUELESS -- after before cleaning: ${musicRepository.getAll().first().size}")
            musicRepository.deleteNotExisting()
            deleteEmptyAlbumsAndArtistsUseCase()

            handlePlaylists(
                lastSyncMillis = lastSyncMillis,
                mergeMode = mergeMode,
            )

            // Update the sync date for the next time.
            cloudPreferencesRepository.setLastSyncMillis(DateUtils.now())
            println("CLUELESS -- after sync: ${musicRepository.getAll().first().size}")
        }

        println("CLUELESS -- result: $result")
        _state.value = if (result.isError()) State.Failure else State.Finish

        return result
    }

    private suspend fun handlePlaylists(
        lastSyncMillis: Long?,
        mergeMode: MergeMode,
    ) {
        val playlistsToSend: List<PlaylistWithMusics> = playlistRepository.getAllToSendToCloud()
        var updatedFromCloud: List<CloudPlaylist> = playlistRepository.fetchUpdatedPlaylistsFromCloud(
            lastSyncMillis = lastSyncMillis,
        )
        println("CLUELESS -- playlists: ${updatedFromCloud.size}")
        if (updatedFromCloud.isNotEmpty()) {
            val names: List<String> = playlistsToSend.map { it.playlist.name }
            val favoriteChanged = playlistsToSend.any { it.playlist.isFavorite }

            updatedFromCloud = updatedFromCloud.filter {
                (it.playlist.name !in names) || if (favoriteChanged) {
                    !it.playlist.isFavorite
                } else {
                    true
                }
            }
        }
        println("CLUELESS -- playlists to send: ${playlistsToSend.size}")
        playlistsToSend.forEach {
            val result = uploadPlaylistToCloudUseCase(
                playlistWithMusics = it,
                mergeMode = mergeMode,
            )
            println("CLUELESS -- result: $result")
        }
        upsertCloudPlaylistUseCase(
            cloudPlaylists = updatedFromCloud,
            mergeMode = mergeMode,
        )
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
        data object ClearingRemoteIds : WorkingState
        data object CheckingMusicsToSend : WorkingState

        data object NoMusicsToSend : EndState

        data class UpdateMusics(override val progress: Float) : ProgressState

        data class UploadMusics(override val progress: Float) : ProgressState

        data object FetchingFromRemote : WorkingState

        data class SavingRemote(override val progress: Float) : ProgressState

        data object Cleaning : WorkingState

        data object Failure : EndState

        data object Finish : EndState
    }
}
