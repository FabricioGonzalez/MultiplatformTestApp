package features.web_locals.details

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import features.navigation.navigateToActressesDetails
import kotlinx.coroutines.flow.collectLatest
import presentation.mvi.use
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.ManagementResourceUiState

data class WebLocalsDetailScreen(
    private val webLocalId: String,
    override val route: String = "VideoDetails",
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    override val key: ScreenKey = "VideoDetails"

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val (state, setEvent, effects) = use(getScreenModel<WebLocalsDetailsViewModel>())

        val sizes = currentWindowAdaptiveInfo()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
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
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    },
                )
            )

            setEvent(WebLocalsDetailsContracts.Event.OnLoadDataRequested(webLocalId = webLocalId))

            effects.collectLatest { effect ->
                when (effect) {
                    WebLocalsDetailsContracts.Effect.BackNavigation -> navigator.pop()
                    is WebLocalsDetailsContracts.Effect.NavigateToActressesRequested -> {
                        navigator.navigateToActressesDetails(
                            actressId = effect.webLocalId,
                            onCompose = onCompose
                        )
                    }
                }
            }
        }

        ManagementResourceUiState(resourceUiState = state.site, successView = { site ->
            Column {
                TextField(site.name, {
                    setEvent(WebLocalsDetailsContracts.Event.OnNameChanged(it))
                })

                TextField(site.url, {
                    setEvent(WebLocalsDetailsContracts.Event.OnUrlChanged(it))
                })

                Button(onClick = {
                    setEvent(WebLocalsDetailsContracts.Event.OnSaveRequested)
                }) {
                    Text("Save")
                }
            }
        })


    }
}

