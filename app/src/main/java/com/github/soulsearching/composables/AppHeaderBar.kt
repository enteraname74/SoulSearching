package com.github.soulsearching.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.github.soulsearching.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeaderBar(
    title : String,
    leftAction : () -> Unit,
    leftIcon : ImageVector = Icons.Default.ArrowBack,
    rightAction : () -> Unit = {},
    rightIcon : ImageVector? = null
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
                    imageVector = leftIcon,
                    contentDescription = stringResource(id = R.string.back_button),
                )
            }
        },
        actions = {
            if (rightIcon != null) {
                IconButton(onClick = rightAction) {
                    Icon(
                        imageVector = rightIcon,
                        contentDescription = stringResource(id = R.string.header_bar_right_button)
                    )
                }
            }
        }
    )
}
