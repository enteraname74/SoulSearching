package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.events.MusicEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.AllMusicsComposable

fun allMusicsTab(
    allMusicsViewModel: AllMusicsViewModel,
    state: AllMusicsState,
    navigateToFolder: (folderPath: String) -> Unit,
    navigateToMonth: (month: String) -> Unit,
): PagerScreen = PagerScreen(
    title = strings.musics,
    screen = {
        AllMusicsComposable(
            musicState = state,
            navigateToFolder = navigateToFolder,
            navigateToMonth = navigateToMonth,
            sortByName = {
                allMusicsViewModel.onMusicEvent(
                    MusicEvent.SetSortType(SortType.NAME)
                )

            },
            sortByDateAction = {
                allMusicsViewModel.onMusicEvent(
                    MusicEvent.SetSortType(SortType.ADDED_DATE)
                )
            },
            sortByMostListenedAction = {
                allMusicsViewModel.onMusicEvent(
                    MusicEvent.SetSortType(SortType.NB_PLAYED)
                )

            },
            setSortDirectionAction = {
                val newDirection =
                    if (state.sortDirection == SortDirection.ASC) {
                        SortDirection.DESC
                    } else {
                        SortDirection.ASC
                    }
                allMusicsViewModel.onMusicEvent(
                    MusicEvent.SetSortDirection(newDirection)
                )
            },
            onLongMusicClick = allMusicsViewModel::showMusicBottomSheet,
        )
    }
)