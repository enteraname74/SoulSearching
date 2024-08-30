package com.github.enteraname74.domain.usecase.musicfolder

import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllMusicFolderListUseCase(
    private val getAllMusicUseCase: GetAllMusicUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<MusicFolderList>> =
        getAllMusicUseCase().mapLatest { allMusics ->
            allMusics.groupBy { it.folder }.entries.map { (folder, musics) ->
                MusicFolderList(
                    path = folder,
                    musics = musics,
                    coverId = musics.firstOrNull { it.coverId != null }?.coverId
                )
            }
        }
}