package com.github.enteraname74.soulsearching.feature.settings.cloud.users

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class SettingsCloudUsersViewHolder(
    private val commonUserUseCase: CommonUserUseCase,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : SoulViewModelHolder<
        SettingsCloudUsersActions,
        SettingsCloudUsersNavScope,
        SettingsCloudUsersState,
        >(
    initialState = SettingsCloudUsersState(
        users = emptyList(),
        dialog = null,
    )
), SettingsCloudUsersActions {
    override val actions: SettingsCloudUsersActions = this

    init {
        viewModelScope.launch {
            feedbackPopUpManager.showErrorIfAny(commonUserUseCase.fetchAll())
        }

        viewModelScope.launch {
            combine(
                commonUserUseCase.observeUser(),
                commonUserUseCase.observeAll(),
            ) { user, allUsers ->
                allUsers.map { simpleUser ->
                    UiSimpleUser(
                        id = simpleUser.id,
                        name = simpleUser.username,
                        onDelete = { showDeleteDialog(simpleUser.id) }.takeIf {
                            user?.isAdmin == true && user.id != simpleUser.id
                        }
                    )
                }
            }.collectLatest { uiUsers ->
                updateState {
                    copy(
                        users = uiUsers,
                    )
                }
            }
        }
    }

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    fun showDeleteDialog(userId: Uuid) {
        updateState {
            copy(
                dialog = object : SoulDialog {
                    @Composable
                    override fun Dialog() {
                        SoulAlertDialog(
                            confirmAction = { deleteUser(userId) },
                            dismissAction = { updateState { copy(dialog = null) } },
                            confirmText = strings.delete,
                            dismissText = strings.cancel,
                            title = strings.cloudUsersDeleteDialogTitle,
                            text = strings.cloudUsersDeleteDialogText,
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

    fun deleteUser(userId: Uuid) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            feedbackPopUpManager.showErrorIfAny(commonUserUseCase.delete(userId))
            updateState { copy(dialog = null) }
        }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudUsersActions,
        state: SettingsCloudUsersState
    ) {
        SettingsCloudUsersScreen(
            actions = actions,
            state = state,
        )
    }
}