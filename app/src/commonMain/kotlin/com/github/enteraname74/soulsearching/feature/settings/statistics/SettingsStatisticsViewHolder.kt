package com.github.enteraname74.soulsearching.feature.settings.statistics

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import com.github.enteraname74.domain.model.statistics.Period
import com.github.enteraname74.domain.model.statistics.PeriodStatistics
import com.github.enteraname74.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodType
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodUiStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.toStatisticsUiElement
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.toStatisticsUiListenedElement
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.toStatisticsUiMostSongsElement
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsStatisticsViewHolder(
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) : SoulViewModelHolderV2<SettingsStatisticsNavScope, SettingsStatisticsState>() {
    override fun getInitialState(): SettingsStatisticsState =
        SettingsStatisticsState(
            selectedPeriod = Period.All,
            availablePeriods = listOf(Period.All),
            currentStatistics = PeriodUiStatistics(
                period = Period.All,
                stats = emptyList(),
            ),
            onNewPeriod = ::setNewPeriod,
            navigateBack = { navigate { navigateBack() } },
            onNewPeriodType = ::onNewPeriodType,
        )

    private val selectedPeriod: MutableStateFlow<Period> = MutableStateFlow(Period.All)

    init {
        statisticsFlow()
        availablePeriodsFlow()
    }

    private fun statisticsFlow() {
        viewModelScope.launch {
            selectedPeriod.collectLatest { period ->
                updateState {
                    copy(
                        currentStatistics = listeningStatisticsRepository.getPeriodStatistics(period).toPeriodUiStatistics(),
                    )
                }
            }
        }
    }

    private fun availablePeriodsFlow() {
        viewModelScope.launch {
            selectedPeriod.collectLatest { period ->
                when (period) {
                    Period.All -> updateState { copy(availablePeriods = listOf(Period.All)) }
                    is Period.Month -> listeningStatisticsRepository.observeAllMonthPeriods().collectLatest { periods ->
                        updateState { copy(availablePeriods = periods) }
                    }
                    is Period.Year -> listeningStatisticsRepository.observeAllYearPeriods().collectLatest { periods ->
                        updateState { copy(availablePeriods = periods) }
                    }
                }
            }
        }
    }

    fun onNewPeriodType(type: PeriodType) {
        viewModelScope.launch {
            val newSelectedPeriod = when (type) {
                PeriodType.All -> Period.All
                PeriodType.Month -> listeningStatisticsRepository.observeAllMonthPeriods().first().last()
                PeriodType.Year -> listeningStatisticsRepository.observeAllYearPeriods().first().last()
            }
            setNewPeriod(newSelectedPeriod)
        }
    }

    fun setNewPeriod(period: Period) {
        selectedPeriod.value = period
        updateState {
            copy(
                selectedPeriod = period,
            )
        }
    }

    private fun PeriodStatistics.toPeriodUiStatistics(): PeriodUiStatistics =
        PeriodUiStatistics(
            period = period,
            stats = listOfNotNull(
                PeriodUiStatistics.Stats(
                    title = strings.mostPlayedSongs,
                    data = mostPlayedMusics.map { pagingData ->
                        pagingData.map { it.toStatisticsUiElement() }
                    },
                ),
                PeriodUiStatistics.Stats(
                    title = strings.mostListenedSongs,
                    data = mostListenedMusics.map { pagingData ->
                        pagingData.map { it.toStatisticsUiListenedElement() }
                    }
                ),
                PeriodUiStatistics.Stats(
                    title = strings.mostPlayedArtists,
                    data = mostPlayedArtists.map { pagingData ->
                        pagingData.map {
                            it.toStatisticsUiElement(
                                onClick = { navigate { toArtist(it.artist.id) } }
                            )
                        }
                    },
                ),
                (this as? PeriodStatistics.All)?.let {
                    PeriodUiStatistics.Stats(
                        title = strings.artistsWithMostSongs,
                        data = artistsWithMostMusics.map { pagingData ->
                            pagingData.map {
                                it.toStatisticsUiMostSongsElement(
                                    onClick = { navigate { toArtist(it.artist.id) } }
                                )
                            }
                        },
                    )
                },
                PeriodUiStatistics.Stats(
                    title = strings.mostPlayedAlbums,
                    data = mostPlayedAlbums.map { pagingData ->
                        pagingData.map {
                            it.toStatisticsUiElement(
                                onClick = { navigate { toAlbum(it.album.id) } }
                            )
                        }
                    },
                ),
                PeriodUiStatistics.Stats(
                    title = strings.mostPlayedPlaylists,
                    data = mostPlayedPlaylists.map { pagingData ->
                        pagingData.map {
                            it.toStatisticsUiElement(
                                onClick = { navigate { toPlaylist(it.playlist.id) } }
                            )
                        }
                    },
                )
            )
        )

    @Composable
    override fun Content(state: SettingsStatisticsState) {
        SettingsStatisticsScreen(state)
    }
}