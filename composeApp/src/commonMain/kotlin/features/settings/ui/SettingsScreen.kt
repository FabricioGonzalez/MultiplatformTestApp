package features.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon


class SettingsScreen(
    override val route: String = "settings",
    override val onCompose: (AppBarState) -> Unit
) : AppScreen {
    override val key: ScreenKey = "Settings"

    @Composable
    @Preview
    override fun Content() {

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

            TabRow(0) {
                Tab(selected = false, onClick = {}, content = {
                    Text("Test")
                })
            }
        }
    }
}