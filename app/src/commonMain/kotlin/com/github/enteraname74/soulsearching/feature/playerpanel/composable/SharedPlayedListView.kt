package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.player.PlayerUserStatus
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.list.ShapeListStyle
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.feature.player.domain.state.SharedListState
import com.github.enteraname74.soulsearching.util.rememberClipboardController

// TODO SHARED PLAYED LIST: actions on users
@Composable
fun SharedPlayedListView(
    state: SharedListState,
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
                    style = ShapeListStyle.Unique
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
                UserRow(
                    modifier = Modifier.padding(1.dp),
                    user = state.guests[index],
                    style = ShapeListStyle.fromList(
                        listSize = state.guests.size,
                        index = index,
                    ),
                )
            }
        }
    }
}

@Composable
private fun InvitationCode(
    code: String,
) {
    val clipboard = rememberClipboardController()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SoulSearchingColorTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp),
            )
            .clickableWithHandCursor { clipboard.copy(code) }
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
    style: ShapeListStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(style.shape())
            .background(SoulSearchingColorTheme.colorScheme.primary)
            .alpha(if (user.status == PlayerUserStatus.Disconnected) UiConstants.ALPHA_DISABLED else 1.0f)
            .padding(all = UiConstants.Spacing.large)
    ) {
        Text(
            text = user.username,
            style = UiConstants.Typography.bodyLarge,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
    }
}