package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.domain.model.player.PlayerUserStatus
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person_remove
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.ext.disableIf
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.list.ShapeListStyle
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.feature.player.domain.state.SharedListState
import com.github.enteraname74.soulsearching.util.rememberClipboardController
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.shared_list_code_description
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.shared_list_code_title
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.shared_list_guests
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.shared_list_host
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec

@Composable
fun SharedPlayedListView(
    state: SharedListState,
    contentColor: Color,
    containerColor: Color,
    secondaryContainerColor: Color,
    secondaryContentColor: Color,
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
                containerColor = secondaryContainerColor,
                contentColor = secondaryContentColor,
            )
        }
        state.host?.let {
            stickyHeader {
                SectionTitle(
                    title = CoreRes.string.shared_list_host.toTextSpec(),
                    containerColor = containerColor,
                    contentColor = contentColor,
                )
            }
            item {
                UserRow(
                    user = it,
                    style = ShapeListStyle.Unique,
                    containerColor = secondaryContainerColor,
                    contentColor = secondaryContentColor,
                )
            }
        }
        if (state.guests.isNotEmpty()) {
            stickyHeader {
                SectionTitle(
                    title = CoreRes.string.shared_list_guests.toTextSpec(),
                    containerColor = containerColor,
                    contentColor = contentColor,
                )
            }
            items(
                count = state.guests.size
            ) { index ->
                UserRow(
                    modifier = Modifier.padding(1.dp),
                    user = state.guests[index],
                    containerColor = secondaryContainerColor,
                    contentColor = secondaryContentColor,
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
    contentColor: Color,
    containerColor: Color,
) {
    val clipboard = rememberClipboardController()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = containerColor,
                shape = RoundedCornerShape(20.dp),
            )
            .clickableWithHandCursor { clipboard.copy(code) }
            .padding(
                all = UiConstants.Spacing.large,
            ),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SoulText(
            text = CoreRes.string.shared_list_code_title.toTextSpec(),
            style = UiConstants.Typography.bodyTitle,
            color = contentColor,
        )
        SoulText(
            text = code.toTextSpec(),
            style = UiConstants.Typography.titleBig,
            color = contentColor,
        )
        SoulText(
            text = CoreRes.string.shared_list_code_description.toTextSpec(),
            style = UiConstants.Typography.bodySmall,
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SectionTitle(
    title: SoulTextSpec,
    containerColor: Color,
    contentColor: Color,
) {
    SoulText(
        modifier = Modifier
            .background(color = containerColor)
            .padding(
                all = UiConstants.Spacing.medium,
            ),
        text = title,
        style = UiConstants.Typography.bodyLarge,
        color = contentColor,
    )
}

@Composable
private fun UserRow(
    user: SharedListState.User,
    style: ShapeListStyle,
    contentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(style.shape())
            .background(
                containerColor
                    .disableIf(user.status == PlayerUserStatus.Disconnected)
            )
            .padding(
                vertical = UiConstants.Spacing.medium,
                horizontal = UiConstants.Spacing.mediumPlus,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
    ) {
        SoulText(
            modifier = Modifier
                .weight(1f),
            text = user.username.toTextSpec(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = UiConstants.Typography.bodyLarge,
            color = contentColor.disableIf(user.status == PlayerUserStatus.Disconnected),
        )
        if (user.onRemove != null) {
            SoulIconButton(
                icon = CoreRes.drawable.ic_person_remove,
                size = UiConstants.ImageSize.medium,
                onClick = user.onRemove,
                colors = SoulButtonDefaults.colors(
                    containerColor = Color.Transparent,
                )
            )
        } else {
            Spacer(
                modifier = Modifier.height(UiConstants.ImageSize.mediumLarge)
            )
        }
    }
}
