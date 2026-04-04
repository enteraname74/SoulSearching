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
    suspend operator fun invoke(): SoulResult<Unit> = SoulResult.runCatching {
        val idsNoLongerOnCloud: List<String> = musicRepository.getDeletedRemoteMusicIds()
        musicRepository.clearRemoteIds(idsNoLongerOnCloud)
        val musicsToSend: List<Music> = musicRepository.getAllToSendToCloud()

        if (musicsToSend.isEmpty()) return@runCatching

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
        val savedRemoteIds: List<String> = musicsToUpdate.mapNotNull { music ->
            updateMusicToCloudUseCase(
                music = music,
                mergeMode = mergeMode,
            )?.remoteId
        } + musicsToUpload.mapNotNull { music ->
            uploadMusicToCloudUseCase(
                music = music,
                mergeMode = mergeMode,
            )?.remoteId
        }

        val cloudPreferences: CloudPreferences? =
            cloudPreferencesRepository.observePreferences().firstOrNull()

        val newSyncedMillis: Long = DateUtils.now()

        // Fetching updated songs from cloud
        val updatedRemoteSongs: List<CloudMusic> = musicRepository.fetchUpdatedSongsFromCloud(
            lastSyncMillis = cloudPreferences?.lastSyncMillis,
        )

        // Keeping only songs not already handled
        val filteredSongsToSave = updatedRemoteSongs.filter { music ->
            savedRemoteIds.none { it == music.fingerprint }
        }
        // Saving each song, with their album and artist
        filteredSongsToSave.forEach {
            upsertCloudMusicUseCase(
                cloudMusic = it,
                mergeMode = mergeMode,
            )
        }
        // Deleting potential empty albums, artists and music (localPath and remoteId null).
        musicRepository.deleteNotExisting()
        deleteEmptyAlbumsAndArtistsUseCase()

        // Update the sync date for the next time.
        cloudPreferencesRepository.setLastSyncMillis(newSyncedMillis)
    }
}