package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.CloudPreferences
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Sync local songs with remote one.
 * The steps are :
 * - sending recently updated songs to remote, or never sent ones
 * - fetching recently updated songs to remote and saving them
 */
class SyncMusicWithCloudUseCase(
    private val musicRepository: MusicRepository,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    private val upsertCloudMusicUseCase: UpsertCloudMusicUseCase,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
    private val updateMusicToCloudUseCase: UpdateMusicToCloudUseCase,
    private val uploadMusicToCloudUseCase: UploadMusicToCloudUseCase,
) {
    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    suspend operator fun invoke(): SoulResult<Unit> {
        val result: SoulResult<Unit> = SoulResult.runCatching {
            _state.value = State.ClearingRemoteIds
            val idsNoLongerOnCloud: List<String> = musicRepository.getDeletedRemoteMusicIds()
            musicRepository.clearRemoteIds(idsNoLongerOnCloud)

            _state.value = State.CheckingMusicsToSend
            val musicsToSend: List<Music> = musicRepository.getAllToSendToCloud()
            if (musicsToSend.isEmpty()) {
                _state.value = State.NoMusicsToSend
            }

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

            val cloudPreferences: CloudPreferences? =
                cloudPreferencesRepository.observePreferences().firstOrNull()

            _state.value = State.FetchingFromRemote
            // Fetching updated songs from cloud
            val updatedRemoteSongs: List<CloudMusic> = musicRepository.fetchUpdatedSongsFromCloud(
                lastSyncMillis = cloudPreferences?.lastSyncMillis,
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
            musicRepository.deleteNotExisting()
            deleteEmptyAlbumsAndArtistsUseCase()

            // Update the sync date for the next time.
            cloudPreferencesRepository.setLastSyncMillis(DateUtils.now())
        }

        _state.value = if (result.isError()) State.Failure else State.Finish

        return result
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
