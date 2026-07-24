package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
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

        val cachedAlbums: MutableSet<Album> = mutableSetOf()
        val cachedArtists: MutableSet<Artist> = mutableSetOf()
        val cachedMusics: MutableSet<Music> = mutableSetOf()

        val updatedMusics = musicsToSend.mapNotNull {
            val music = uploadMusicToCloudUseCase(
                music = it,
                mergeMode = MergeMode.LocalFirst,
                cachedArtists = cachedArtists,
                cachedAlbums = cachedAlbums,
                cachedMusics = cachedMusics,
            )
            music?.let {
                cachedAlbums += music.album
                cachedArtists += music.artists
                cachedMusics += music
            }
            music
        }
        musicRepository.upsertAll(updatedMusics)

        // We want to keep the order of the musics that the user selected.
        val finalList: List<Music> = (updatedMusics + alreadySyncMusics).distinctBy { it.musicId }
        val byIds = finalList.associateBy { it.musicId }

        return musicIds.mapNotNull { byIds[it] }
    }
}
