package features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.ui.AutoSizeImage
import presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.ManagementResourceUiState
import domain.model.VideoEntity
import features.videos.video_details.VideoDetailScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.ui.common.AppScreen
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
        /* ActionBar(
             *//*character = state.video,*//*
            favorite = state.isFavorite,
            onActionFavorite = { setEvent(HomeContract.Event.OnFavoriteClick) },
            onBackPressed = { setEvent(HomeContract.Event.OnBackPressed) }
            )*/
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
                    VideoDetails(modifier = Modifier.height(180.dp).width(120.dp).clickable {
                        setEvent(HomeContract.Event.OnVideoItemClicked(video.id))
                    }, video = video)
                }
            }
        }
    }

}

@Composable
fun VideoDetails(modifier: Modifier = Modifier, video: VideoEntity) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(36.dp)),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AutoSizeImage(
            url = video.photo,
            modifier = Modifier.fillMaxWidth().weight(weight = 0.8f, fill = true),
            contentScale = ContentScale.Fit,
            contentDescription = null
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = video.title,
            style = MaterialTheme.typography.titleSmall
        )
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
    MediaAppTheme {
        HomeLayout(setEvent = {}, state =  HomeContract.State(ResourceUiState.Success(flow {
            emit(
                PagingData.from(
                    listOf(
                        VideoEntity(cursor = "", id = "", title = "Teste", photo = "")
                    )
                )
            )
        }), isFavorite = ResourceUiState.Success(true)))
    }
}
