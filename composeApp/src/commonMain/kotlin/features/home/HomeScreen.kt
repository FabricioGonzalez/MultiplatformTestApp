package features.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import features.home.components.VideosListFeed
import features.videos.video_details.VideoDetailScreen
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.state.ManagementResourceUiState

class HomeScreen(
    override val route: String = "home",
    override val onCompose: (AppBarState) -> Unit
) : AppScreen {
    override val key: ScreenKey = "Home"

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val homeViewModel = getScreenModel<HomeViewModel>()
        val sizes = calculateWindowSizeClass()
        val state by homeViewModel.uiState.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
            onCompose(
                AppBarState(
                    title = null,
                    actions = null,
                    navigationIcon = null,
                    searchBar = null,
                    snackbarHost = null,
                )
            )
            homeViewModel.setEvent(HomeContract.Event.OnLoadDataRequested)

            homeViewModel.effect.collectLatest { effect ->
                when (effect) {
                    HomeContract.Effect.CharacterAdded -> snackbarHostState.showSnackbar("Character added to favorites")

                    HomeContract.Effect.CharacterRemoved -> snackbarHostState.showSnackbar("Character removed from favorites")

                    HomeContract.Effect.BackNavigation -> {
                        navigator.pop()
                    }

                    is HomeContract.Effect.NavigateToDetails -> {
                        navigator.push(VideoDetailScreen(effect.id, onCompose = onCompose))
                    }

                }
            }
        }
        HomeLayout(
            modifier = Modifier.fillMaxSize(),
            setEvent = homeViewModel::setEvent,
            state = state,
            windowSizeClass = sizes
        )
    }

}

@Composable
fun HomeLayout(
    modifier: Modifier = Modifier,
    setEvent: (HomeContract.Event) -> Unit,
    state: HomeContract.State,
    windowSizeClass: WindowSizeClass
) {
    ManagementResourceUiState(
        modifier = modifier,
        resourceUiState = state.videoFeeds,
        successView = { videos ->
            VideosListFeed(windowSizeClass = windowSizeClass,
                videos = videos,
                onSweetsSelected = { setEvent(HomeContract.Event.OnVideoItemClicked(it.id)) })
        },
        onTryAgain = { setEvent(HomeContract.Event.OnTryCheckAgainClick) },
        onCheckAgain = { setEvent(HomeContract.Event.OnTryCheckAgainClick) },
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun HomePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.height(900.dp)) {
            /*HomeLayout(
                modifier = Modifier.fillMaxSize(),
                setEvent = {},
                windowSizeClass = calculateWindowSizeClass(),
                state = HomeContract.State(ResourceUiState.Success(flow {
                    emit(
                        PagingData.from(
                            listOf(
                                VideoEntity(cursor = "", id = "1", title = "Teste", photo = ""),
                                VideoEntity(cursor = "", id = "2", title = "Teste", photo = ""),
                                VideoEntity(cursor = "", id = "3", title = "Teste", photo = ""),
                                VideoEntity(cursor = "", id = "4", title = "Teste", photo = ""),
                                VideoEntity(cursor = "", id = "5", title = "Teste", photo = ""),
                                VideoEntity(cursor = "", id = "6", title = "Teste", photo = ""),
                                VideoEntity(cursor = "", id = "7", title = "Teste", photo = ""),
                                VideoEntity(cursor = "", id = "8", title = "Teste", photo = ""),
                            )
                        )
                    )
                }), isFavorite = ResourceUiState.Success(true))
            )*/
        }
    }
}
