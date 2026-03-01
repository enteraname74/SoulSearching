package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_north
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_pin_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_schedule
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_sort_by_alpha
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_south
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SortOptionsComposable(
    setSortType: (SortType) -> Unit,
    toggleSortDirection: () -> Unit,
    sortType: SortType,
    sortDirection: SortDirection,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors()
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    Box {
        SoulSegmentedButton(
            colors = colors,
            buttons = listOf(
                SoulSegmentedIconButton(
                    data = sortType.icon(),
                    contentPadding = SoulButtonDefaults.contentPadding(
                        horizontal = 0.dp,
                    ),
                    onClick = { isExpanded = !isExpanded },
                ),
                SoulSegmentedIconButton(
                    data = sortDirection.icon(),
                    contentPadding = SoulButtonDefaults.contentPadding(
                        horizontal = 0.dp,
                    ),
                    onClick = toggleSortDirection,
                )
            )
        )
        DropdownMenu(
            expanded = isExpanded,
            containerColor = colors.containerColor,
            onDismissRequest = { isExpanded = false },
        ) {
            SortType.entries.forEach { type ->
                SortTypeItem(
                    sortType = type,
                    contentColor = colors.contentColor,
                    onClick = {
                        setSortType(type)
                        isExpanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SortTypeItem(
    sortType: SortType,
    onClick: () -> Unit,
    contentColor: Color,
) {
    DropdownMenuItem(
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand),
        text = {
            Text(
                text = sortType.title(),
                color = contentColor,
                style = UiConstants.Typography.body,
            )
        },
        leadingIcon = {
            SoulIcon(
                icon = sortType.icon(),
                color = contentColor,
            )
        },
        onClick = onClick,
    )
}

private fun SortType.icon(): DrawableResource =
    when (this) {
        SortType.NAME -> CoreRes.drawable.ic_sort_by_alpha
        SortType.ADDED_DATE -> CoreRes.drawable.ic_schedule
        SortType.NB_PLAYED -> CoreRes.drawable.ic_pin_filled
    }

@Composable
private fun SortType.title(): String =
    when (this) {
        SortType.NAME -> strings.sortByName
        SortType.ADDED_DATE -> strings.sortByAddedDate
        SortType.NB_PLAYED -> strings.sortByMostListened
    }

private fun SortDirection.icon(): DrawableResource =
    when (this) {
        SortDirection.ASC -> CoreRes.drawable.ic_north
        SortDirection.DESC -> CoreRes.drawable.ic_south
    }