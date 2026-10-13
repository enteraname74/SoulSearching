package com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementView
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.ModifyCollectionViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionState

@Composable
fun ModifyCollectionRoute(
    viewModel: ModifyCollectionViewModel,
    onNavigationState: (ModifyCollectionNavigationState) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val navigationState by viewModel.navigationState.collectAsState()
    val bottomSheetState: SoulBottomSheet? by viewModel.bottomSheetState.collectAsState()
    bottomSheetState?.BottomSheet()

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    when {
        state is ModifyCollectionState.Data && formState is ModifyCollectionFormState.Data -> {
            EditableElementView(
                title = strings.collectionInformation,
                coverSectionTitle = strings.collectionCover,
                editableElement = (state as ModifyCollectionState.Data).editableElement,
                navigateBack = viewModel::navigateBack,
                onSelectCover = viewModel::showCoversBottomSheet,
                onValidateModification = viewModel::updateCollection,
                textFields = (formState as ModifyCollectionFormState.Data).textFields,
            )
        }
        state is ModifyCollectionState.Loading -> SoulLoadingScreen(navigateBack = viewModel::navigateBack)
    }
}
