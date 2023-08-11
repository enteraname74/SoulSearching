package com.github.soulsearching.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.font.FontWeight
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun SubMenuComposable(
    title: String,
    sortByDateAction: () -> Unit,
    sortByMostListenedAction: () -> Unit,
    sortByName: () -> Unit,
    setSortTypeAction: () -> Unit,
    rightComposable: @Composable (() -> Unit),
    createPlaylistComposable: @Composable (() -> Unit) = {},
    sortType: Int,
    sortDirection: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = DynamicColor.primary)
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
            SortOptionsComposable(
                sortByDateAction = sortByDateAction,
                sortByMostListenedAction = sortByMostListenedAction,
                sortByName = sortByName,
                setSortTypeAction = setSortTypeAction,
                sortDirection = sortDirection,
                sortType = sortType
            )
            rightComposable()
        }
    }
}