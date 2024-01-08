package com.github.soulsearching.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.BuildConfig
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.setting.SettingsElement
import com.github.soulsearching.theme.DynamicColor

@Composable
fun SettingsAboutScreen(
    finishAction: () -> Unit,
    navigateToDevelopers: () -> Unit
) {
    var clickCount by rememberSaveable {
        mutableIntStateOf(0)
    }
    val context = LocalContext.current
    val versionName = BuildConfig.VERSION_NAME

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.about_title),
            leftAction = finishAction
        )
        LazyColumn {
            item {
                SettingsElement(
                    title = stringResource(id = R.string.developers_title),
                    text = stringResource(id = R.string.developers_text),
                    clickAction = navigateToDevelopers
                )
            }
            item {
                SettingsElement(
                    title = stringResource(id = R.string.version_name_title),
                    text = versionName,
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
