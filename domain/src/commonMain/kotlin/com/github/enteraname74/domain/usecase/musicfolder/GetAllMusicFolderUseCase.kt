package com.github.enteraname74.domain.usecase.musicfolder

import com.github.enteraname74.domain.model.MusicFolder
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllMusicFolderUseCase(
    private val getAllMusicUseCase: GetAllMusicUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<MusicFolder>> =
        getAllMusicUseCase().mapLatest { allMusics ->
            allMusics.groupBy { it.folder }.entries.map { (folder, musics) ->
                MusicFolder(
                    path = folder,
                    allMusicsSize = musics.size,
                    coverId = musics.firstOrNull { it.coverId != null }?.coverId
                )
            }
        }
}