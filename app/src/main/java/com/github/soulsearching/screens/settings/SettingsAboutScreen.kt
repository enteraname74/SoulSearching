package com.github.soulsearching.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.BuildConfig
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.Developer
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.SettingsElement
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsAboutScreen(
    finishAction: () -> Unit
) {
    var clickCount by rememberSaveable {
        mutableStateOf(0)
    }
    val context = LocalContext.current
    val versionName = BuildConfig.VERSION_NAME

    val developers = listOf(
        Developer(
            name = "Noah Penin",
            function = stringResource(id = R.string.lead_developer)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.about_title),
            leftAction = finishAction
        )
        LazyColumn(
            modifier = Modifier.padding(
                Constants.Spacing.medium
            )
        ) {
            item {
                SettingsElement(
                    title = stringResource(id = R.string.developers_title),
                    text = stringResource(id = R.string.developers_text),
                    padding = PaddingValues(
                        start = Constants.Spacing.large,
                        top = Constants.Spacing.large,
                        end = Constants.Spacing.large,
                        bottom = Constants.Spacing.medium,
                    )
                )
            }
            items(developers) {developer ->
                SettingsElement(
                    title = developer.name,
                    text = developer.function,
                    padding = PaddingValues(
                        start = Constants.Spacing.extraLarge,
                        end = Constants.Spacing.medium,
                        top = Constants.Spacing.medium,
                        bottom = Constants.Spacing.medium
                    )
                )
            }
            item {
                SettingsElement(
                    title = stringResource(id = R.string.version_name_title),
                    text = versionName,
                    padding = PaddingValues(
                        start = Constants.Spacing.large,
                        end = Constants.Spacing.large,
                        top = Constants.Spacing.veryLarge,
                        bottom = Constants.Spacing.large
                    ),
                    clickAction = {
                        clickCount += 1
                        if (clickCount == 10) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.version_name_action_text),
                                Toast.LENGTH_SHORT
                            ).show()
                            clickCount = 0
                        }
                    }
                )
            }
            item {
                PlayerSpacer()
            }
        }
    }
}
