package features.actresses.actress_picture_search

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
import androidx.navigation.NavHostController
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import com.multiplatform.webview.setting.PlatformWebSettings
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.flow.collectLatest
import presentation.mvi.use
import presentation.navigation.AppScreenDestinations
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon

data class ActressPictureSearchScreen(
    private val actressName: String, override val route: String = "Picture Search",
    private val navController: NavHostController,
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {

    override val key: ScreenKey = "ActressPictureSearch"

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val (state, setEvent, effect) = use(getScreenModel<ActressPictureSearchViewModel>())

        val webviewContent =
            rememberWebViewState("https://www.pornpics.com/?q=$actressName").apply {
                webSettings.isJavaScriptEnabled = true
                webSettings.androidWebSettings.apply {
                    safeBrowsingEnabled = false
                    allowFileAccess = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = true
                    isAlgorithmicDarkeningAllowed = true
                    layerType = PlatformWebSettings.AndroidWebSettings.LayerType.SOFTWARE
                }
                webSettings.allowFileAccessFromFileURLs = true
                webSettings.allowUniversalAccessFromFileURLs = true
            }
        val webViewNavigator = rememberWebViewNavigator()



        LaunchedEffect(key1 = Unit) {
            onCompose(
                AppBarState(
                    title = null,
                    actions = null,
                    navigationIcon = {
                        ArrowBackIcon {
                            if (!webViewNavigator.canGoBack) {
                                navController.navigateUp()
                            } else webViewNavigator.navigateBack()
                        }
                    },
                    searchBar = null,
                    snackbarHost = null,
                )
            )

            effect.collectLatest { effect ->
                when (effect) {
                    ActressPictureSearchContracts.Effect.CharacterAdded -> snackbarHostState.showSnackbar(
                        "Character added to favorites"
                    )

                    ActressPictureSearchContracts.Effect.CharacterRemoved -> snackbarHostState.showSnackbar(
                        "Character removed from favorites"
                    )

                    ActressPictureSearchContracts.Effect.BackNavigation -> navController.navigateUp()
                    is ActressPictureSearchContracts.Effect.NavigateToActressesRequested -> {
                        navController.navigate(
                            AppScreenDestinations.ActressDetails(actressId = effect.id)
                        )
                    }

                    is ActressPictureSearchContracts.Effect.PlayVideoRequested -> {}
                }
            }
        }

        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TextField(
                readOnly = true,
                value = webviewContent.lastLoadedUrl ?: "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )

            WebView(
                state = webviewContent,
                navigator = webViewNavigator,
                modifier = Modifier.fillMaxWidth()
                    .weight(0.95f)
            )
        }
    }
}
