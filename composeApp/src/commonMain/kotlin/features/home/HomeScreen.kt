package features.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import features.home.components.PortraitSweetsCard
import features.home.components.VideosListFeed
import features.home.components.rememberColumns
import features.navigation.navigateToVideoDetails
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.mvi.use
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.state.ManagementResourceUiState

class HomeScreen(
    override val route: String = "home", override val onCompose: (AppBarState) -> Unit
) : AppScreen {
    override val key: ScreenKey = "Home"

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val (state, setEvent, effect) = use(getScreenModel<HomeViewModel>())
        val sizes = calculateWindowSizeClass()
        val snackbarHostState = remember { SnackbarHostState() }

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
            onCompose(
                AppBarState(
                    title = null,
                    actions = null,
                    navigationIcon = null,
                    searchBar = null,
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    },
                )
            )
            setEvent(HomeContract.Event.OnLoadDataRequested)

            effect.collectLatest { effect ->
                when (effect) {
                    HomeContract.Effect.CharacterAdded -> snackbarHostState.showSnackbar("Character added to favorites")

                    HomeContract.Effect.CharacterRemoved -> snackbarHostState.showSnackbar("Character removed from favorites")

                    HomeContract.Effect.BackNavigation -> {
                        navigator.pop()
                    }

                    is HomeContract.Effect.NavigateToDetails -> {
                        navigator.navigateToVideoDetails(videoId = effect.id, onCompose = onCompose)
                    }

                }
            }
        }
        HomeLayout(
            modifier = Modifier.fillMaxSize(),
            setEvent = setEvent,
            state = state,
            windowSizeClass = sizes
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLayout(
    modifier: Modifier = Modifier,
    setEvent: (HomeContract.Event) -> Unit,
    state: HomeContract.State,
    windowSizeClass: WindowSizeClass
) {
    Column {
        DockedSearchBar(
            modifier = Modifier.fillMaxWidth().padding(8.dp, 4.dp),
            query = state.searchText,
            onQueryChange = {
                setEvent(HomeContract.Event.OnSearchTextChanged(it))
            },
            onActiveChange = {},
            onSearch = {
                setEvent(HomeContract.Event.OnSearchTextChanged(it))
            },
            content = {},
            active = false
        )

        if (state.searchText.isNotEmpty()) {
            ManagementResourceUiState(
                modifier = modifier,
                resourceUiState = state.searchFeed,
                successView = { videos ->
                    val items = videos.collectAsLazyPagingItems()
                    LazyVerticalGrid(
                        rememberColumns(windowSizeClass), modifier = Modifier.fillMaxSize()
                    ) {
                        items(items.itemCount,
                            contentType = { "sweets" },
                            key = { item -> items[item]!!.id }) { item ->
                            PortraitSweetsCard(sweets = items[item]!!,
                                isLoading = items.loadState.refresh is LoadState.Loading,
                                onClick = { setEvent(HomeContract.Event.OnVideoItemClicked(items[item]!!.id)) })
                        }
                    }
                },
                onTryAgain = { setEvent(HomeContract.Event.OnTryCheckAgainClick) },
                onCheckAgain = { setEvent(HomeContract.Event.OnTryCheckAgainClick) },
            )
        } else ManagementResourceUiState(
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
}

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
