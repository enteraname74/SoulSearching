package com.github.enteraname74.domain.usecase.month

import com.github.enteraname74.domain.ext.coverFromSongs
import com.github.enteraname74.domain.model.MonthMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllMonthMusicUseCase(
    private val getAllMusicsSortedUseCase: GetAllMusicsSortedUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<MonthMusics>> =
        getAllMusicsSortedUseCase(
            sortDirection = SortDirection.DESC,
            sortType = SortType.ADDED_DATE,
        ).mapLatest { allMusics ->
            allMusics.groupBy { DateUtils.getMonthAndYearOfDate(date = it.addedDate) }.entries.map { (date, musics) ->
                MonthMusics(
                    month = date,
                    allMusicsSize = musics.size,
                    cover = musics.coverFromSongs()
                )
            }
        }
}