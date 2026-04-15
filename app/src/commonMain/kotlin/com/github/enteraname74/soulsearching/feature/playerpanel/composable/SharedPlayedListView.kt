package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.feature.player.domain.state.SharedListState

// TODO SHARED PLAYED LIST: copy code to clipboard
// TODO SHARED PLAYED LIST: actions on users
@Composable
fun SharedPlayedListView(
    state: SharedListState
) {
    LazyColumnCompat(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            start = UiConstants.Spacing.mediumPlus,
            end = UiConstants.Spacing.mediumPlus,
            bottom = getNavigationBarPadding().toDp()
        )
    ) {
        item {
            InvitationCode(
                code = state.code,
            )
        }
        state.host?.let {
            stickyHeader {
                SectionTitle(
                    title = strings.sharedListHost,
                )
            }
            item {
                UserRow(
                    user = it,
                    style = UserRowStyle.Unique
                )
            }
        }
        if (state.guests.isNotEmpty()) {
            stickyHeader {
                SectionTitle(
                    title = strings.sharedListGuests,
                )
            }
            items(
                count = state.guests.size
            ) { index ->
                val style = when {
                    state.guests.size == 1 -> UserRowStyle.Unique
                    index == 0 -> UserRowStyle.Top
                    index == state.guests.lastIndex -> UserRowStyle.Bottom
                    else -> UserRowStyle.Body
                }
                UserRow(
                    modifier = Modifier.padding(1.dp),
                    user = state.guests[index],
                    style = style,
                )
            }
        }
    }
}

@Composable
private fun InvitationCode(
    code: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SoulSearchingColorTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(
                all = UiConstants.Spacing.large,
            ),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = strings.sharedListCodeTitle,
            style = UiConstants.Typography.bodyTitle,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
        Text(
            text = code,
            style = UiConstants.Typography.titleBig,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
        Text(
            text = strings.sharedListCodeDescription,
            style = UiConstants.Typography.bodySmall,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
) {
    Text(
        modifier = Modifier
            .background(
                color = SoulSearchingColorTheme.colorScheme.secondary,
            )
            .padding(
                all = UiConstants.Spacing.medium,
            ),
        text = title,
        style = UiConstants.Typography.bodyLarge,
        color = SoulSearchingColorTheme.colorScheme.onSecondary,
    )
}

@Composable
private fun UserRow(
    user: SharedListState.User,
    style: UserRowStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(style.shape())
            .background(SoulSearchingColorTheme.colorScheme.primary)
            .padding(all = UiConstants.Spacing.large)
    ) {
        Text(
            text = user.username,
            style = UiConstants.Typography.bodyLarge,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
    }
}

private enum class UserRowStyle {
    Top,
    Body,
    Unique,
    Bottom;

    fun shape(): Shape =
        when (this) {
            Top -> RoundedCornerShape(
                topStart = CORNER_BIG,
                topEnd = CORNER_BIG,
                bottomStart = CORNER_SMALL,
                bottomEnd = CORNER_SMALL,
            )
            Unique -> RoundedCornerShape(
                size = CORNER_BIG,
            )
            Body -> RoundedCornerShape(
                size = CORNER_SMALL,
            )
            Bottom -> RoundedCornerShape(
                topStart = CORNER_SMALL,
                topEnd = CORNER_SMALL,
                bottomStart = CORNER_BIG,
                bottomEnd = CORNER_BIG,
            )
        }
}

private val CORNER_BIG: Dp = 16.dp
private val CORNER_SMALL: Dp = 6.dp
