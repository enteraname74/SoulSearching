package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.AllMusicsComposable

fun allMusicsTab(
    mainPageViewModel: MainPageViewModel,
    navigateToMonth: (month: String) -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.MUSICS,
    screen = {
        val musicState: AllMusicsState by mainPageViewModel.allMusicsState.collectAsState()

        AllMusicsComposable(
            musicState = musicState,
            navigateToMonth = navigateToMonth,
            setSortType = mainPageViewModel::setMusicSortType,
            toggleSortDirection = {
                val newDirection =
                    if (musicState.sortDirection == SortDirection.ASC) {
                        SortDirection.DESC
                    } else {
                        SortDirection.ASC
                    }
               mainPageViewModel.setMusicSortDirection(newDirection)
            },
            onLongClick = mainPageViewModel::showMusicBottomSheet,
        )
    }
)