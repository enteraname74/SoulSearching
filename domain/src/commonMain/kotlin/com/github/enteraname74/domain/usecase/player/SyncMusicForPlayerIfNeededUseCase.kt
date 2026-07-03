package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.music.UploadMusicToCloudUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class SyncMusicForPlayerIfNeededUseCase(
    private val musicRepository: MusicRepository,
    private val uploadMusicToCloudUseCase: UploadMusicToCloudUseCase,
) {
    suspend operator fun invoke(musicIds: List<Uuid>): List<Music> {
        val idsNoLongerOnCloud: List<String> = musicRepository.getDeletedRemoteMusicIds()
        musicRepository.clearRemoteIds(idsNoLongerOnCloud)

        val musics = musicRepository.getFromIds(musicIds).firstOrNull() ?: return emptyList()
        val alreadySyncMusics = musics.filter { it.remoteId != null }

        val musicsToSend = musics.filter {
            it.remoteId == null
        }

        val updatedMusics = musicsToSend.mapNotNull {
            uploadMusicToCloudUseCase(
                music = it,
                mergeMode = MergeMode.LocalFirst,
            )
        }

        return (updatedMusics + alreadySyncMusics).distinctBy { it.musicId }
    }
}
