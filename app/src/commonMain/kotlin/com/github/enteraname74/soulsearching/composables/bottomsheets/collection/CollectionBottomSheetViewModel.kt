package com.github.enteraname74.soulsearching.composables.bottomsheets.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteCollectionDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiCollectionDialog
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.Scope
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.model.player.PlayedListScope
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.soulsearching.domain.usecase.collection.CommonCollectionUseCase
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class CollectionBottomSheetViewModel(
    private val commonCollectionUseCase: CommonCollectionUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: CollectionBottomSheetNavScope,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    settings: SoulSearchingSettings,
    params: CollectionBottomSheetDestination,
) : ViewModel() {
    private val collectionIds: List<Uuid> = params.collectionIds
    private val dialogState = MutableStateFlow<SoulDialog?>(null)

    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<CollectionBottomSheetState> = combine(
        commonCollectionUseCase.getFromIds(collectionIds),
        playbackManager.playedList,
        dialogState,
        settings.getFlowOn(SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN),
        hasValidCloudInformationUseCase(),
        playbackManager.currentScope,
    ) { data ->
        val collections = data[0] as List<CollectionWithMusics>
        val playedList = data[1] as List<Music>
        val dialog = data[2] as SoulDialog?
        val isQuickAccessShown = data[3] as Boolean
        val hasValidCloudInformation = data[4] as Boolean
        val playedListScope = data[5] as PlayedListScope?

        CollectionBottomSheetState(
            collections = collections,
            bottomSheetTopInformation = buildTopInformation(collections),
            rowSpecs = buildRowSpecs(
                collections = collections,
                playedList = playedList,
                isQuickAccessShown = isQuickAccessShown,
                hasValidCloudInformation = hasValidCloudInformation,
                playedListScope = playedListScope,
            ),
            dialogState = dialog,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CollectionBottomSheetState(),
    )

    private fun buildRowSpecs(
        collections: List<CollectionWithMusics>,
        playedList: List<Music>,
        isQuickAccessShown: Boolean,
        hasValidCloudInformation: Boolean,
        playedListScope: PlayedListScope?,
    ): List<BottomSheetRowSpec> = buildList {
        if (collections.isEmpty()) return@buildList

        val musics = collections.allMusics()
        val hasMusics = musics.isNotEmpty()
        val hasUserMusics = musics.any { it.scope == Scope.User }

        if (isQuickAccessShown) {
            val isInQuickAccess = collections.all { it.collection.isInQuickAccess }
            add(
                BottomSheetRowSpec.quickAccess(
                    onClick = { handleQuickAccess(!isInQuickAccess) },
                    isInQuickAccess = isInQuickAccess,
                )
            )
        }

        if (hasUserMusics) {
            add(BottomSheetRowSpec.addToPlaylist(::addToPlaylists))
        }

        if (hasMusics && playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.playNext(::playNext))
        }

        if (hasMusics) {
            add(BottomSheetRowSpec.addToQueue(::addToQueue))
        }

        if (hasValidCloudInformation && hasUserMusics && playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.startSharedPlayedList(::startSharedPlayedList))
        }

        if (playedList.isNotEmpty()) {
            add(BottomSheetRowSpec.removeFromPlayedList(::removeFromPlayedList))
        }

        add(
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_delete_filled,
                title = if (collections.size == 1) {
                    strings.deleteCollection
                } else {
                    strings.deleteSelectedCollections
                },
                onClick = ::showDeleteDialog,
            )
        )
    }

    private fun buildTopInformation(
        collections: List<CollectionWithMusics>,
    ): BottomSheetTopInformation = if (collections.size == 1) {
        val collection = collections.first()
        BottomSheetTopInformation(
            title = collection.collection.name,
            subTitle = strings.musics(collection.musics.size),
            cover = null,
        )
    } else {
        BottomSheetTopInformation(
            title = strings.multipleSelection,
            subTitle = strings.selectedElements(collections.size),
            cover = null,
        )
    }

    private fun showDeleteDialog() {
        dialogState.value = if (state.value.collections.size == 1) {
            DeleteCollectionDialog(
                onDelete = ::deleteCollections,
                onClose = { dialogState.value = null },
            )
        } else {
            DeleteMultiCollectionDialog(
                onDelete = ::deleteCollections,
                onClose = { dialogState.value = null },
            )
        }
    }

    private fun deleteCollections() {
        viewModelScope.launch {
            dialogState.value = null
            loadingManager.withLoading {
                val result = commonCollectionUseCase.deleteAll(collectionIds)
                feedbackPopUpManager.showErrorIfAny(result)
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonCollectionUseCase.upsertAll(
                    state.value.collections.map {
                        it.collection.copy(isInQuickAccess = newValue)
                    }
                )
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun playNext() {
        performPlaybackAction { musics ->
            playbackManager.addMultipleMusicsToPlayNext(musics)
        }
    }

    private fun addToQueue() {
        performPlaybackAction { musics ->
            playbackManager.addMultipleMusicsToQueue(musics)
        }
    }

    private fun performPlaybackAction(
        action: suspend (List<Music>) -> SoulResult<Unit>,
    ) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = action(state.value.collections.allMusics())
            if (result.isError()) {
                feedbackPopUpManager.showErrorIfAny(result)
            } else {
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun addToPlaylists() {
        navScope.toAddToPlaylists(state.value.collections.allMusics().map { it.musicId })
    }

    private fun removeFromPlayedList() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.removeSongsFromPlayedList(
                musicIds = state.value.collections.allMusics().map { it.musicId },
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
            val musicIds = state.value.collections.allMusics()
                .filter { it.scope != Scope.SharedPlayedList }
                .map { it.musicId }
            when (val result = playbackManager.startSharedList(musicIds)) {
                is SoulResult.Error -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> {
                    multiSelectionManager.clearMultiSelection()
                    navScope.navigateBack()
                }
            }
        }
    }

    private fun List<CollectionWithMusics>.allMusics(): List<Music> =
        flatMap { it.musics }.distinctBy { it.musicId }
}
