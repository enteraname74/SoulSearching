package com.github.enteraname74.soulsearching.composables.bottomsheets.month

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMusicsDialog
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.uuid.Uuid

class MonthBottomSheetViewModel(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: MonthBottomSheetNavScope,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    params: MonthBottomSheetDestination,
) : ViewModel() {
    private val monthKeys: List<String> = params.months

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    private val months: Flow<List<MonthMusicsPreview>> =
        if (monthKeys.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(
                monthKeys.map { commonMusicUseCase.getMonthMusicPreview(it) }
            ) { previews ->
                previews.filterNotNull()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val musics: Flow<List<Music>> = months.flatMapLatest {
        flow {
            emit(loadSelectedMusics())
        }
    }

    @Suppress("UNCHECKED_CAST")
    val state = combine(
        months,
        musics,
        playbackManager.playedList,
        dialogState,
        hasValidCloudInformationUseCase(),
        playbackManager.currentScope,
    ) { data ->
        val months = data[0] as List<MonthMusicsPreview>
        val musics = data[1] as List<Music>
        val playedList = data[2] as List<Music>
        val dialogState = data[3] as SoulDialog?
        val hasValidCloudInformation = data[4] as Boolean
        val playedListScope = data[5] as PlayedListScope?

        MonthBottomSheetState(
            months = months,
            musics = musics,
            bottomSheetTopInformation = buildTopInformation(months),
            rowSpecs = buildRowSpecs(
                musics = musics,
                playedList = playedList,
                hasValidCloudInformation = hasValidCloudInformation,
                playedListScope = playedListScope,
            ),
            dialogState = dialogState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MonthBottomSheetState(),
    )

    private suspend fun loadSelectedMusics(): List<Music> =
        monthKeys
            .flatMap { commonMusicUseCase.getAllMusicFromMonth(it) }
            .distinctBy { it.musicId }

    private fun buildRowSpecs(
        musics: List<Music>,
        playedList: List<Music>,
        hasValidCloudInformation: Boolean,
        playedListScope: PlayedListScope?,
    ): List<BottomSheetRowSpec> = buildList {
        if (musics.isEmpty()) return@buildList

        val hasUserMusics: Boolean = musics.any { it.scope == Scope.User }

        if (hasUserMusics) {
            add(BottomSheetRowSpec.addToPlaylist(::addToPlaylists))
        }

        if (playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.playNext(::playNext))
        }

        add(BottomSheetRowSpec.addToQueue(::addToQueue))

        if (hasValidCloudInformation && hasUserMusics && playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.startSharedPlayedList(::startSharedPlayedList))
        }

        if (playedList.isNotEmpty()) {
            add(BottomSheetRowSpec.removeFromPlayedList(::removeFromPlayedList))
        }

        if (hasUserMusics) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_delete_filled,
                    title = strings.deleteMonthMusics,
                    onClick = ::showDeleteDialog,
                )
            )
        }
    }

    private fun buildTopInformation(months: List<MonthMusicsPreview>): BottomSheetTopInformation =
        if (months.size == 1) {
            val month = months.first()
            BottomSheetTopInformation(
                title = month.month,
                subTitle = strings.musics(total = month.totalMusics),
                cover = month.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = months.size),
                cover = null,
            )
        }

    private fun addToPlaylists() {
        navScope.toAddToPlaylists(state.value.musics.map { it.musicId })
    }

    private fun playNext() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.addMultipleMusicsToPlayNext(
                musics = state.value.musics,
            )
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun addToQueue() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.addMultipleMusicsToQueue(
                musics = state.value.musics,
            )
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun removeFromPlayedList() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val musicIds: List<Uuid> = state.value.musics.map { it.musicId }

            val result = playbackManager.removeSongsFromPlayedList(
                musicIds = musicIds,
            )
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun startSharedPlayedList() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val musicIds: List<Uuid> =
                state.value.musics
                    .filter { it.scope != Scope.SharedPlayedList }
                    .map { it.musicId }

            val result = playbackManager.startSharedList(
                musicIds = musicIds,
            )
            when (result) {
                is SoulResult.Error -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> {
                    multiSelectionManager.clearMultiSelection()
                    navScope.navigateBack()
                }
            }
        }
    }

    private fun showDeleteDialog() {
        dialogState.value = DeleteMusicsDialog(
            onDelete = ::deleteMusics,
            onClose = { dialogState.value = null },
            title = strings.deleteMonthMusicsDialogTitle,
            text = strings.deleteMonthMusicsDialogText,
        )
    }

    private fun deleteMusics() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            dialogState.value = null
            val musicIds: List<Uuid> = state.value.musics.map { it.musicId }
            when (val result = playbackManager.removeSongsFromPlayedList(musicIds)) {
                is SoulResult.Error<*> -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> {
                    deleteMusicUseCase(musicIds = musicIds)
                    multiSelectionManager.clearMultiSelection()
                    navScope.navigateBack()
                }
            }
        }
    }
}
