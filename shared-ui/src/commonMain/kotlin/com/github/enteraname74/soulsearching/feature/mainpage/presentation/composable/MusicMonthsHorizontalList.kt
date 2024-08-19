package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.MonthMusics
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun MusicMonthsHorizontalList(
    months: List<MonthMusics>,
    onMonthClicked: (month: String) -> Unit = {},
    onMonthLongClicked: (month: String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = UiConstants.Spacing.veryLarge),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        Text(
            modifier = Modifier
                .padding(start = UiConstants.Spacing.medium),
            text = strings.byMonths,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        if (months.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                contentPadding = PaddingValues(
                    start = UiConstants.Spacing.medium,
                    end = UiConstants.Spacing.medium
                )
            ) {
                items(
                    items = months
                ) { element ->
                    BigPreviewComposable(
                        coverId = element.coverId,
                        title = element.month,
                        text = strings.musics(total = element.allMusicsSize),
                        onClick = {
                            onMonthClicked(element.month)
                        },
                        onLongClick = { onMonthLongClicked(element.month) }
                    )
                }
            }
        } else {
            NoElementView()
        }
    }
}