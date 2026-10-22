package com.github.enteraname74.soulsearching.composables.bottomsheets.collection.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulCheckBox
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_add
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview

@Composable
fun AddToCollectionBottomSheetScreen(viewModel: AddToCollectionBottomSheetViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    state.dialogState?.Dialog()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.secondary)
            .padding(UiConstants.Spacing.medium),
    ) {
        SoulTopBar(
            withStatusBarPadding = false,
            title = strings.addToCollection,
            leftAction = TopBarNavigationAction(onClick = viewModel::navigateBack),
            rightAction = TopBarValidateAction(onClick = viewModel::confirm),
            colors = SoulTopBarDefaults.primary(
                containerColor = Color.Transparent,
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            ),
        )
        LazyColumnCompat(modifier = Modifier.fillMaxWidth()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableWithHandCursor(onClick = viewModel::showCreateCollectionDialog)
                        .padding(vertical = UiConstants.Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    SoulIcon(
                        icon = CoreRes.drawable.ic_add,
                        color = SoulSearchingColorTheme.colorScheme.onSecondary,
                        size = UiConstants.ImageSize.medium,
                    )
                    Text(
                        text = strings.createCollectionDialogTitle,
                        color = SoulSearchingColorTheme.colorScheme.onSecondary,
                        style = UiConstants.Typography.bodyTitle,
                    )
                }
            }
            items(state.collections, key = { it.id }) { collection ->
                CollectionSelectable(
                    collection = collection,
                    isSelected = collection.id in state.selectedCollectionIds,
                    onClick = { viewModel.toggleSelection(collection.id) },
                )
            }
            item { SoulPlayerSpacer() }
        }
    }
}

@Composable
private fun CollectionSelectable(
    collection: CollectionPreview,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val color = SoulSearchingColorTheme.colorScheme.onSecondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithHandCursor(onClick = onClick)
            .padding(UiConstants.Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            SoulImage(cover = collection.cover, size = UiConstants.CoverSize.small, tint = color)
            Text(
                text = collection.name,
                color = color,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        SoulCheckBox(checked = isSelected, onCheckedChange = { onClick() }, color = color)
    }
}
