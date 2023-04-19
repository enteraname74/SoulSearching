package com.github.soulsearching.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R

@Composable
fun SortOptionsComposable(
    sortByDateAction : () -> Unit,
    sortByMostListenedAction : () -> Unit,
    sortByName : () -> Unit,
    setSortTypeAction : () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.small)
        ) {
            Icon(
                modifier = Modifier
                    .clickable { sortByDateAction() }
                    .padding(Constants.Spacing.small)
                    .size(
                        Constants.ImageSize.small
                    ),
                imageVector = Icons.Default.Schedule,
                contentDescription = stringResource(id = R.string.sort_by_date_added),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Icon(
                modifier = Modifier
                    .clickable { sortByDateAction() }
                    .padding(Constants.Spacing.small)
                    .size(
                        Constants.ImageSize.small
                    ),
                imageVector = Icons.Default.Pin,
                contentDescription = stringResource(id = R.string.sort_by_most_listened),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Icon(
                modifier = Modifier
                    .clickable { sortByDateAction() }
                    .padding(Constants.Spacing.small)
                    .size(
                        Constants.ImageSize.small
                    ),
                imageVector = Icons.Default.SortByAlpha,
                contentDescription = stringResource(id = R.string.sort_by_name),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Icon(
            modifier = Modifier
                .clickable { sortByDateAction() }
                .padding(Constants.Spacing.small)
                .size(
                    Constants.ImageSize.small
                ),
            imageVector = Icons.Default.North,
            contentDescription = stringResource(id = R.string.sort_by_asc_or_dsc),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}