package com.github.soulsearching.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun SubMenuComposable(
    modifier: Modifier = Modifier,
    title: String,
    sortByDateAction: () -> Unit,
    sortByMostListenedAction: () -> Unit,
    sortByName: () -> Unit,
    setSortDirectionAction: () -> Unit,
    rightComposable: @Composable (() -> Unit),
    createPlaylistComposable: @Composable (() -> Unit) = {},
    sortType: Int,
    sortDirection: Int,
    isUsingSort: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(color = DynamicColor.primary)
            .clickable { }
            .padding(
                top = Constants.Spacing.medium,
                start = Constants.Spacing.medium,
                end = Constants.Spacing.medium
            )
            .composed {
                modifier
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = DynamicColor.onPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
        ) {
            createPlaylistComposable()
            if (isUsingSort) {
                SortOptionsComposable(
                    sortByDateAction = sortByDateAction,
                    sortByMostListenedAction = sortByMostListenedAction,
                    sortByName = sortByName,
                    setSortDirection = setSortDirectionAction,
                    sortDirection = sortDirection,
                    sortType = sortType
                )
            }
            rightComposable()
        }
    }
}