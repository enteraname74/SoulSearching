package com.github.enteraname74.domain.usecase.month

import com.github.enteraname74.domain.model.MonthMusicList
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetMonthMusicListUseCase(
    private val getAllMusicsUseCase: GetAllMusicUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(month: String): Flow<MonthMusicList?> =
        getAllMusicsUseCase().mapLatest { allMusics ->
            val musics = allMusics.filter { DateUtils.getMonthAndYearOfDate(date = it.addedDate) == month  }
            MonthMusicList(
                month = month,
                musics = musics,
                coverId = musics.firstOrNull { it.coverId != null }?.coverId
            )
        }
}