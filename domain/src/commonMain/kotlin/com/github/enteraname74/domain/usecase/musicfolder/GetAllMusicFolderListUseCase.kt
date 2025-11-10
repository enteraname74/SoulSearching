package com.github.enteraname74.domain.usecase.musicfolder

import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllMusicFolderListUseCase(
    private val musicRepository: MusicRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<MusicFolderList>> =
        musicRepository.getAll().mapLatest { allMusics ->
            allMusics.groupBy { it.folder }.entries.map { (folder, musics) ->
                MusicFolderList(
                    path = folder,
                    musics = musics,
                )
            }
        }
}