package com.github.enteraname74.domain.usecase.musicfolder

import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetMusicFolderListUseCase(
    private val getAllMusicUseCase: GetAllMusicUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(path: String): Flow<MusicFolderList?> =
        getAllMusicUseCase().mapLatest { allMusics ->
            val musics = allMusics.filter { it.folder == path }
            MusicFolderList(
                path = path,
                musics = musics,
                coverId = musics.firstOrNull { it.coverId != null }?.coverId
            )
        }
}