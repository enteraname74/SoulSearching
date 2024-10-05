package com.github.enteraname74.domain.usecase.month

import com.github.enteraname74.domain.ext.coverFromSongs
import com.github.enteraname74.domain.model.MonthMusics
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllMonthMusicUseCase(
    private val getAllMusicUseCase: GetAllMusicUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<MonthMusics>> =
        getAllMusicUseCase().mapLatest { allMusics ->
            allMusics.groupBy { DateUtils.getMonthAndYearOfDate(date = it.addedDate) }.entries.map { (date, musics) ->
                MonthMusics(
                    month = date,
                    allMusicsSize = musics.size,
                    cover = musics.coverFromSongs()
                )
            }
        }
}