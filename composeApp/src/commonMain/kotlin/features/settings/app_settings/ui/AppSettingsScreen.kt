package features.settings.app_settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.mvi.use
import presentation.ui.common.AppBarState
import presentation.ui.common.AppTab
import presentation.ui.common.ArrowBackIcon


class AppSettingsScreen(
    override val route: String = "appSettings",
    override val onCompose: (AppBarState) -> Unit
) : AppTab {
    override val key: ScreenKey = "appSettings"

    override val options: TabOptions
        @Composable
        get() {
            val title = "Settings"
            val icon = rememberVectorPainter(Icons.Default.Settings)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    @Preview
    override fun Content() {

        val (state, setEvent, effects) = use(getScreenModel<AppSettingsViewModel>())

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            onCompose(
                AppBarState(
                    title = null,
                    actions = null,
                    navigationIcon = {
                        ArrowBackIcon {
                            navigator.pop()
                        }
                    },
                    searchBar = null,
                    snackbarHost = null,
                )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {


        }
    }
}