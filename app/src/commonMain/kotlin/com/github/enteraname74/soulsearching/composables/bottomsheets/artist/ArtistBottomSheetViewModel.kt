package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteArtistDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiArtistDialog
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class ArtistBottomSheetViewModel(
    private val commonArtistUseCase: CommonArtistUseCase,
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: ArtistBottomSheetNavScope,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    settings: SoulSearchingSettings,
    params:  ArtistBottomSheetDestination,
) : ViewModel() {
    private val artistIds: List<Uuid> = params.artistIds

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    @Suppress("UNCHECKED_CAST")
    val state: StateFlow<ArtistBottomSheetState> = combine(
        commonArtistUseCase.getFromIds(artistIds),
        playbackManager.playedList,
        dialogState,
        settings.getFlowOn(
            settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
        ),
        hasValidCloudInformationUseCase(),
        playbackManager.currentScope,
    ) { data ->
        val artists = data[0] as List<ArtistWithMusics>
        val playedList = data[1] as List<Music>
        val dialogState = data[2] as SoulDialog?
        val isQuickAccessShown = data[3] as Boolean
        val hasValidCloudInformation = data[4] as Boolean
        val playedListScope = data[5] as PlayedListScope?

        ArtistBottomSheetState(
            artists = artists,
            bottomSheetTopInformation = buildTopInformation(artists),
            rowSpecs = buildRowSpecs(
                artists = artists,
                isQuickAccessShown = isQuickAccessShown,
                hasValidCloudInformation = hasValidCloudInformation,
                playedList = playedList,
                playedListScope = playedListScope,
            ),
            dialogState = dialogState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ArtistBottomSheetState(),
    )

    private fun buildRowSpecs(
        artists: List<ArtistWithMusics>,
        playedList: List<Music>,
        isQuickAccessShown: Boolean,
        hasValidCloudInformation: Boolean,
        playedListScope: PlayedListScope?,
    ) : List<BottomSheetRowSpec> = buildList {
        if (artists.isEmpty()) return@buildList

        val editEnabled: Boolean = artists.size == 1
        val hasUserMusics: Boolean = artists.any { artist ->
            artist.musics.any { it.scope == Scope.User }
        }

        if (isQuickAccessShown) {
            val isInQuickAccess: Boolean = if (artists.size == 1) {
                artists.first().artist.isInQuickAccess
            } else {
                artists.all { it.artist.isInQuickAccess }
            }

            add(
                BottomSheetRowSpec.quickAccess(
                    onClick = { handleQuickAccess(!isInQuickAccess) },
                    isInQuickAccess = isInQuickAccess,
                )
            )
        }

        if (editEnabled) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_edit_filled,
                    title = strings.modifyArtist,
                    onClick = ::toModifyArtist,
                )
            )
        }

        if (playedListScope?.isRemote != true) {
            add(BottomSheetRowSpec.playNext(::playNext))
        }

        add(BottomSheetRowSpec.addToQueue(::addToQueue))

        if (hasValidCloudInformation && hasUserMusics) {
            add(BottomSheetRowSpec.startSharedPlayedList(::startSharedPlayedList))
        }

        if (playedList.isNotEmpty()) {
            add(
                BottomSheetRowSpec.removeFromPlayedList(::removeFromPlayedList),
            )
        }

        add(
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_delete_filled,
                title = if (artists.size == 1) {
                    strings.deleteArtist
                } else {
                    strings.deleteSelectedArtists
                },
                onClick = ::showDeleteDialog,
            )
        )
    }

    private fun toModifyArtist() {
        artistIds.firstOrNull()?.let {
            viewModelScope.launch {
                multiSelectionManager.clearMultiSelection()
                navScope.toModifyArtist(it)
            }
        }
    }

    private fun buildTopInformation(artists: List<ArtistWithMusics>): BottomSheetTopInformation =
        if (artists.size == 1) {
            val artist = artists.first()
            BottomSheetTopInformation(
                title = artist.artist.artistName,
                subTitle = strings.musics(total = artist.musics.filter { !it.isHidden }.size),
                cover = artist.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = artists.size),
                cover = null,
            )
        }

    private fun showDeleteDialog() {
        dialogState.value = if (state.value.artists.size == 1) {
            DeleteArtistDialog(
                onDelete = ::deleteArtists,
                onClose = { dialogState.value = null },
            )
        } else {
            DeleteMultiArtistDialog(
                onDelete = ::deleteArtists,
                onClose = { dialogState.value = null },
            )
        }
    }

    private fun deleteArtists() {
        viewModelScope.launch {
            dialogState.value = null
            loadingManager.withLoading {
                deleteArtistUseCase(state.value.artists)
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonArtistUseCase.upsertAll(
                    allArtists = state.value.artists.map {
                        it.artist.copy(
                            isInQuickAccess = newValue,
                        )
                    }
                )
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun playNext() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val musics: List<Music> =
                state.value.artists
                    .flatMap { it.musics }
                    .distinctBy { it.musicId }

            val result = playbackManager.addMultipleMusicsToPlayNext(
                musics = musics,
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
            val musics: List<Music> =
                state.value.artists
                    .flatMap { it.musics }
                    .distinctBy { it.musicId }

            val result = playbackManager.addMultipleMusicsToQueue(
                musics = musics,
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
            val musicIds: List<Uuid> =
                state.value.artists
                    .flatMap { it.musics }
                    .distinctBy { it.musicId }
                    .map { it.musicId }

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
                state.value.artists
                    .flatMap { it.musics }
                    .filter { it.scope != Scope.SharedPlayedList }
                    .distinctBy { it.musicId }
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
}
