package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_add
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllCollectionsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageListPaged
import com.github.enteraname74.soulsearching.feature.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import kotlin.uuid.Uuid

fun allCollectionsTab(
    mainPageViewModel: MainPageViewModel,
    navigateToCollection: (collectionId: Uuid) -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.COLLECTIONS,
    screen = {
        val state: AllCollectionsState by mainPageViewModel.allCollectionsState.collectAsState()
        val multiSelectionState: MultiSelectionState by mainPageViewModel.multiSelectionState.collectAsState()
        val collections = state.collections.collectAsLazyPagingItems()

        MainPageListPaged(
            list = collections,
            title = CoreRes.string.collections.toTextSpec(),
            rightComposable = {
                SoulIconButton(
                    onClick = mainPageViewModel::showCreateCollectionDialog,
                    icon = CoreRes.drawable.ic_add,
                    contentDescription = CoreRes.string.create_collection_dialog_title.toTextSpec().string,
                    size = UiConstants.ImageSize.medium,
                )
            },
            setSortType = mainPageViewModel::setCollectionSortType,
            toggleSortDirection = {
                mainPageViewModel.setCollectionSortDirection(
                    if (state.sortDirection == SortDirection.ASC) SortDirection.DESC else SortDirection.ASC
                )
            },
            sortType = state.sortType,
            sortDirection = state.sortDirection,
            key = { it?.id },
            contentType = ALL_COLLECTIONS_CONTENT_TYPE,
            emptyTitle = CoreRes.string.empty_collections.toTextSpec(),
            emptyDescription = CoreRes.string.empty_collections_explanations.toTextSpec(),
        ) { collection ->
            BigPreviewComposable(
                modifier = Modifier.animateItem(),
                cover = collection.cover,
                title = collection.name.toTextSpec(),
                text = SoulTexts.musics(collection.totalMusics),
                imageSize = null,
                onClick = { navigateToCollection(collection.id) },
                onLongClick = {
                    mainPageViewModel.toggleElementInSelection(
                        id = collection.id.toString(),
                        mode = SelectionMode.Collection,
                    )
                },
                isSelected = collection.id.toString() in multiSelectionState.selectedIds,
                isSelectionModeOn = multiSelectionState.totalSelected > 0,
            )
        }
    },
)

private const val ALL_COLLECTIONS_CONTENT_TYPE = "ALL_COLLECTIONS_CONTENT_TYPE"
