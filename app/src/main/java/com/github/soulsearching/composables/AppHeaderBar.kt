package com.github.soulsearching.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.github.soulsearching.R

@Composable
fun AppHeaderBar(
    title : String,
    leftAction : () -> Unit,
    topLeftIcon : ImageVector = Icons.Default.ArrowBack,
    rightAction : () -> Unit = {},
    topRightIcon : ImageVector? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        navigationIcon = {
            IconButton(onClick = leftAction) {
                Icon(
                    imageVector = topLeftIcon,
                    contentDescription = stringResource(id = R.string.back_button),
                )
            }
        },
        actions = {
            if (topRightIcon != null) {
                IconButton(onClick = rightAction) {
                    Icon(
                        imageVector = topRightIcon,
                        contentDescription = stringResource(id = R.string.header_bar_right_button)
                    )
                }
            }
        }
    )
}
