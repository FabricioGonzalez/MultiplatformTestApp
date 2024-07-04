package features.webview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.multiplatform.webview.web.WebContent
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import features.actresses.actress_details.ActressDetailsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon

data class WebviewScreen(
    private val actressName: String, override val route: String = "Webview Screen",
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {

    override val key: ScreenKey = "Webview Screen"

    private val webviewState: WebViewState =
        WebViewState(WebContent.Url("https://www.pornpics.com/?q=$actressName"))
    private val webViewNavigator: WebViewNavigator =
        WebViewNavigator(CoroutineScope(Dispatchers.IO))

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val screenModel = getScreenModel<WebviewViewModel>()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
            onCompose(
                AppBarState(
                    title = null,
                    actions = null,
                    navigationIcon = {
                        ArrowBackIcon {
                            if (!webViewNavigator.canGoBack) {
                                navigator.pop()
                            } else webViewNavigator.navigateBack()
                        }
                    },
                    searchBar = null,
                    snackbarHost = null,
                )
            )

            screenModel.effect.collectLatest { effect ->
                when (effect) {
                    WebviewContracts.Effect.CharacterAdded -> snackbarHostState.showSnackbar("Character added to favorites")

                    WebviewContracts.Effect.CharacterRemoved -> snackbarHostState.showSnackbar("Character removed from favorites")

                    WebviewContracts.Effect.BackNavigation -> navigator.pop()
                    is WebviewContracts.Effect.NavigateToActressesRequested -> {
                        navigator.push(ActressDetailsScreen(effect.id, onCompose = onCompose))
                    }

                    is WebviewContracts.Effect.PlayVideoRequested -> {}
                }
            }
        }

        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TextField(
                readOnly = true,
                value = webviewState.lastLoadedUrl ?: "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )

            WebView(
                state = webviewState,
                navigator = webViewNavigator,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
