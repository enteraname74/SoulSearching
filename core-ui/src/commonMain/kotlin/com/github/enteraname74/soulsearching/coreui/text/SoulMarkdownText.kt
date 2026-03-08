package com.github.enteraname74.soulsearching.coreui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

@Composable
fun SoulMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    Markdown(
        modifier = modifier,
        content = text,
        colors = markdownColor(
            text = textColor
        ),
        typography = markdownTypography(
            h1 = UiConstants.Typography.titleSmall,
            h2 = UiConstants.Typography.titleSmall,
            h3 = UiConstants.Typography.bodyTitle,
            h4 = UiConstants.Typography.bodyMediumTitle,
            h5 = UiConstants.Typography.bodyLarge,
            h6 = UiConstants.Typography.body,
            text = UiConstants.Typography.body,
            code = UiConstants.Typography.body,
            inlineCode = UiConstants.Typography.body,
            quote = UiConstants.Typography.body,
            paragraph = UiConstants.Typography.body,
            ordered = UiConstants.Typography.body,
            bullet = UiConstants.Typography.body,
            list = UiConstants.Typography.body,
            textLink = TextLinkStyles(
                UiConstants.Typography.body.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                ).toSpanStyle()
            ),
            table = UiConstants.Typography.body
        )
    )
}