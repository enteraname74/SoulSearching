package com.github.enteraname74.domain.usecase.month

import com.github.enteraname74.domain.model.MonthMusicList
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetMonthMusicListUseCase(
    private val getAllMusicsSortedUseCase: GetAllMusicsSortedUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(month: String): Flow<MonthMusicList?> =
        getAllMusicsSortedUseCase(
            sortDirection = SortDirection.DESC,
            sortType = SortType.ADDED_DATE,
        ).mapLatest { allMusics ->
            val musics = allMusics.filter { DateUtils.getMonthAndYearOfDate(date = it.addedDate) == month  }
            MonthMusicList(
                month = month,
                musics = musics,
            )
        }
}