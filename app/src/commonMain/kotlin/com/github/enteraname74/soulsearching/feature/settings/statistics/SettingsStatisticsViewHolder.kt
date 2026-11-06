package com.github.enteraname74.soulsearching.feature.settings.statistics

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import com.github.enteraname74.soulsearching.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.domain.model.statistics.PeriodPagedStatistics
import com.github.enteraname74.soulsearching.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.ListStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodGeneralUiStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodType
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.toStatisticsUiElement
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.toStatisticsUiListenedElement
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.toStatisticsUiMostSongsElement
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsStatisticsViewHolder(
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) : SoulViewModelHolderV2<SettingsStatisticsNavScope, SettingsStatisticsState>() {
    private val selectedMusicsChoiceType: MutableStateFlow<ListStatistics.Choices.Type.Musics> =
        MutableStateFlow(ListStatistics.Choices.Type.Musics.MostPlays)

    private val selectedArtistsChoiceType: MutableStateFlow<ListStatistics.Choices.Type.Artists> =
        MutableStateFlow(ListStatistics.Choices.Type.Artists.MostPlays)

    override fun getInitialState(): SettingsStatisticsState =
        SettingsStatisticsState(
            selectedPeriod = Period.All,
            availablePeriods = listOf(Period.All),
            listStatistics = emptyList(),
            onNewPeriod = ::setNewPeriod,
            navigateBack = { navigate { navigateBack() } },
            onNewPeriodType = ::onNewPeriodType,
            periodGeneralUiStatistics = null,
            focusedListStatistics = ListStatistics.Type.Musics,
            onListStatisticsFocused = { updateState { copy(focusedListStatistics = it) } },
        )

    private val selectedPeriod: MutableStateFlow<Period> = MutableStateFlow(Period.All)

    init {
        listStatisticsFlow()
        availablePeriodsFlow()
        generalStatisticsFlow()
    }

    private fun listStatisticsFlow() {
        viewModelScope.launch {
            combine(
                selectedMusicsChoiceType,
                selectedArtistsChoiceType,
                selectedPeriod,
            ) { musicsChoiceType, artistsChoiceType, period ->
                currentState.copy(
                    listStatistics = listeningStatisticsRepository.getPeriodStatistics(period).toListStatistics(
                        musicsChoiceType = musicsChoiceType,
                        artistsChoiceType = artistsChoiceType,
                    )
                )
            }.collectLatest {
                updateState { it }
            }
        }
    }

    private fun generalStatisticsFlow() {
        viewModelScope.launch {
            selectedPeriod.collectLatest { period ->
                listeningStatisticsRepository.observePeriodGeneralStatistics(period).collectLatest { generalStatistics ->
                    updateState {
                        copy(
                            periodGeneralUiStatistics = PeriodGeneralUiStatistics.new(generalStatistics)
                        )
                    }
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

    private fun PeriodPagedStatistics.toListStatistics(
        musicsChoiceType: ListStatistics.Choices.Type.Musics,
        artistsChoiceType: ListStatistics.Choices.Type.Artists,
    ): List<ListStatistics> = listOf(
        ListStatistics.Musics(
            list = when (musicsChoiceType) {
                ListStatistics.Choices.Type.Musics.MostPlays -> mostPlayedMusics.map { pagingData ->
                    pagingData.map { it.toStatisticsUiElement() }
                }
                ListStatistics.Choices.Type.Musics.MostListened -> mostListenedMusics.map { pagingData ->
                    pagingData.map { it.toStatisticsUiListenedElement() }
                }
            },
            currentChoice = musicsChoiceType,
            onChoiceChange = { choice ->
                (choice as? ListStatistics.Choices.Type.Musics)?.let {
                    selectedMusicsChoiceType.value = it
                }
            },
        ),
        ListStatistics.Artists(
            list = when (artistsChoiceType) {
                ListStatistics.Choices.Type.Artists.MostPlays -> mostPlayedArtists.map { pagingData ->
                    pagingData.map {
                        it.toStatisticsUiElement(
                            onClick = { navigate { toArtist(it.artist.id) } }
                        )
                    }
                }
                ListStatistics.Choices.Type.Artists.MostSongs -> (this as? PeriodPagedStatistics.All)?.let {
                    artistsWithMostMusics.map { pagingData ->
                        pagingData.map {
                            it.toStatisticsUiMostSongsElement(
                                onClick = { navigate { toArtist(it.artist.id) } }
                            )
                        }
                    }
                } ?: emptyFlow()
            },
            currentChoice = artistsChoiceType,
            onChoiceChange = { choice ->
                (choice as? ListStatistics.Choices.Type.Artists)?.let {
                    selectedArtistsChoiceType.value = it
                }
            },
            choices = listOfNotNull(
                ListStatistics.Choices.Type.Artists.MostPlays,
                ListStatistics.Choices.Type.Artists.MostSongs.takeIf { this is PeriodPagedStatistics.All },
            )
        ),
        ListStatistics.Albums(
            list = mostPlayedAlbums.map { pagingData ->
                pagingData.map {
                    it.toStatisticsUiElement(
                        onClick = { navigate { toAlbum(it.album.id) } }
                    )
                }
            }
        ),
        ListStatistics.Playlists(
            list = mostPlayedPlaylists.map { pagingData ->
                pagingData.map {
                    it.toStatisticsUiElement(
                        onClick = { navigate { toPlaylist(it.playlist.id) } }
                    )
                }
            }
        ),
        ListStatistics.Collections(
            list = mostPlayedCollections.map { pagingData ->
                pagingData.map {
                    it.toStatisticsUiElement(
                        onClick = { navigate { toCollection(it.collection.id) } }
                    )
                }
            }
        ),
    )

    @Composable
    override fun Content(state: SettingsStatisticsState) {
        SettingsStatisticsScreen(state)
    }
}