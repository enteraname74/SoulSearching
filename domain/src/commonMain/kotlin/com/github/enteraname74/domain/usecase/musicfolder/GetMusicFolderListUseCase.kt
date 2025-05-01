package com.github.enteraname74.domain.usecase.musicfolder

import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetMusicFolderListUseCase(
    private val musicRepository: MusicRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(path: String): Flow<MusicFolderList?> =
        musicRepository.getAll().mapLatest { allMusics ->
            val musics = allMusics.filter { it.folder == path }

            if (musics.isEmpty()) return@mapLatest null

            MusicFolderList(
                path = path,
                musics = musics,
            )
        }
}