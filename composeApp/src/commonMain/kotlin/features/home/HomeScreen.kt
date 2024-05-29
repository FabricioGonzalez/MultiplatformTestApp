package features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import daniel.avila.rnm.kmm.presentation.ui.common.ArrowBackIcon
import domain.model.VideoEntity
import features.home.components.VideoDetails
import features.videos.video_details.VideoDetailScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.model.ResourceUiState
import presentation.ui.common.AppScreen
import presentation.ui.common.state.ManagementResourceUiState
import themes.MediaAppTheme

class HomeScreen(override val route: String = "home") : AppScreen {
    override val key: ScreenKey = "Home"

    @Composable
    override fun Content() {
        val homeViewModel =
            getScreenModel<HomeViewModel>()

        val state by homeViewModel.uiState.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
            homeViewModel.effect.collectLatest { effect ->
                when (effect) {
                    HomeContract.Effect.CharacterAdded ->
                        snackbarHostState.showSnackbar("Character added to favorites")

                    HomeContract.Effect.CharacterRemoved ->
                        snackbarHostState.showSnackbar("Character removed from favorites")

                    HomeContract.Effect.BackNavigation -> {
                        navigator.pop()
                    }

                    is HomeContract.Effect.NavigateToDetails -> {
                        navigator.push(VideoDetailScreen(effect.id))
                    }

                }
            }
        }
        HomeLayout(modifier = Modifier.fillMaxSize(), setEvent = homeViewModel::setEvent, state = state)
    }

    @Composable
    override fun TopBarContent() {
    }
}

@Composable
fun HomeLayout(
    modifier: Modifier = Modifier,
    setEvent: (HomeContract.Event) -> Unit,
    state: HomeContract.State
) {
    ManagementResourceUiState(
        modifier = modifier,
        resourceUiState = state.videos,
        successView = { videos ->
            VideosList(videos.collectAsLazyPagingItems(), setEvent = setEvent)
        },
        onTryAgain = { setEvent(HomeContract.Event.OnTryCheckAgainClick) },
        onCheckAgain = { setEvent(HomeContract.Event.OnTryCheckAgainClick) },
    )
}

@Composable
fun VideosList(videos: LazyPagingItems<VideoEntity>, setEvent: (HomeContract.Event) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Recents")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            items(videos.itemCount) { entity ->
                videos[entity]?.let { video ->
                    VideoDetails(modifier = Modifier.height(180.dp).width(180.dp).clickable {
                        setEvent(HomeContract.Event.OnVideoItemClicked(video.id))
                    }, video = video)
                }
            }
        }
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBar(
    /*character: ResourceUiState<List<VideoEntity>>,*/
    favorite: ResourceUiState<Boolean>,
    onActionFavorite: () -> Unit,
    onBackPressed: () -> Unit,
) {
    TopAppBar(
        title = {
            /* ManagementResourceUiState(
                 resourceUiState = character,
                 successView = {*//* Text(text = it.title)*//* },
                loadingView = { Text(text = "....") },
                onCheckAgain = {},
                onTryAgain = {}
            )*/
        },
        navigationIcon = { ArrowBackIcon(onBackPressed) }
    )
}

@Preview
@Composable
fun HomePreview() {
    MediaAppTheme(content = {
        HomeLayout(setEvent = {}, state = HomeContract.State(ResourceUiState.Success(flow {
            emit(
                PagingData.from(
                    listOf(
                        VideoEntity(cursor = "", id = "", title = "Teste", photo = "")
                    )
                )
            )
        }), isFavorite = ResourceUiState.Success(true)))
    }, appColor = null)
}
