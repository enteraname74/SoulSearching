package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.usecase.player.JoinSharedPlayedListUseCase
import com.github.enteraname74.domain.util.DateUtils
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class SettingsCloudSharedListViewHolder(
    private val playerRepository: PlayerRepository,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val joinSharedPlayedListUseCase: JoinSharedPlayedListUseCase,
) : SoulViewModelHolder<
        SettingsCloudSharedListActions,
        SettingsCloudSharedListNavScope,
        SettingsCloudSharedListState,
        >(
            initialState = SettingsCloudSharedListState(
                previews = emptyList(),
                dialog = null,
            )
        ), SettingsCloudSharedListActions {
    override val actions: SettingsCloudSharedListActions = this

    init {
        fetchData()

        viewModelScope.launch {
            playerRepository
                .observeAllSharedPlayedListPreview()
                .collectLatest { previews ->
                    updateState {
                        copy(
                            previews = previews.map { preview ->
                                UiListPreview(
                                    id = preview.id,
                                    date = DateUtils.formatToReadableDate(preview.createdAtMillis),
                                    allUsers = strings.sharedListPreviewUsers(preview),
                                    connectedUsers = strings.sharedListPreviewConnectedUsers(preview),
                                    onDelete = { showDeleteDialog(preview.id) }.takeIf { preview.isOwner },
                                    onJoin = { join(preview.code) }
                                )
                            }
                        )
                    }
                }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            feedbackPopUpManager.showErrorIfAny(playerRepository.fetchUserListWhereIsIn())
        }
    }

    private fun join(code: String) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            when (val result = joinSharedPlayedListUseCase(code = code)) {
                is SoulResult.Error -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> fetchData()
            }
        }
    }

    override fun toJoin() {
        navigate { toJoin() }
    }

    private fun showDeleteDialog(listId: Uuid) {
        updateState {
            copy(
                dialog = object : SoulDialog {
                    @Composable
                    override fun Dialog() {
                        SoulAlertDialog(
                            confirmAction = { deleteList(listId) },
                            dismissAction = { updateState { copy(dialog = null) } },
                            confirmText = strings.delete,
                            dismissText = strings.cancel,
                            title = strings.sharedListDeleteTitle,
                            text = strings.sharedListDeleteText,
                            icon = {
                                SoulIcon(
                                    icon = CoreRes.drawable.ic_delete_filled,
                                    contentDescription = strings.delete,
                                )
                            }
                        )
                    }
                }
            )
        }
    }

    private fun deleteList(listId: Uuid) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            feedbackPopUpManager.showErrorIfAny(playerRepository.deleteSharedListAndSync(listId = listId))
            updateState { copy(dialog = null) }
        }
    }

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudSharedListActions,
        state: SettingsCloudSharedListState
    ) {
        SettingsCloudSharedListScreen(
            actions = actions,
            state = state,
        )
    }
}