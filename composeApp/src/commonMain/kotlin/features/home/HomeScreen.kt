package features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.ui.AutoSizeImage
import daniel.avila.rnm.kmm.presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.ui.common.ActionBarIcon
import daniel.avila.rnm.kmm.presentation.ui.common.ArrowBackIcon
import daniel.avila.rnm.kmm.presentation.ui.common.state.ManagementResourceUiState
import domain.model.VideoEntity
import kotlinx.coroutines.flow.collectLatest

class HomeScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

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

                    HomeContract.Effect.BackNavigation -> navigator.pop()
                }
            }
        }

        Scaffold(
            topBar = {
                ActionBar(
                    /*character = state.video,*/
                    favorite = state.isFavorite,
                    onActionFavorite = { homeViewModel.setEvent(HomeContract.Event.OnFavoriteClick) },
                    onBackPressed = { homeViewModel.setEvent(HomeContract.Event.OnBackPressed) }
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState, snackbar = { data ->
                    Snackbar(data)
                })
            }
        ) { padding ->
            ManagementResourceUiState(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                resourceUiState = state.videos,
                successView = { videos ->
                    VideosList(videos.collectAsLazyPagingItems())
                },
                onTryAgain = { homeViewModel.setEvent(HomeContract.Event.OnTryCheckAgainClick) },
                onCheckAgain = { homeViewModel.setEvent(HomeContract.Event.OnTryCheckAgainClick) },
            )
        }
    }
}

@Composable
fun VideosList(videos: LazyPagingItems<VideoEntity>) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Recents")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            items(videos.itemCount) { entity ->
                videos[entity]?.let { video ->
                    VideoDetails(modifier = Modifier.height(180.dp).width(120.dp), video = video)
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
        navigationIcon = { ArrowBackIcon(onBackPressed) },
        actions = {
            ManagementResourceUiState(
                resourceUiState = favorite,
                successView = {
                    ActionBarIcon(
                        onClick = onActionFavorite,
                        icon = if (it) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                    )
                },
                loadingView = {
                    ActionBarIcon(
                        enabled = false,
                        onClick = {},
                        icon = Icons.Filled.Favorite
                    )
                },
                onCheckAgain = {},
                onTryAgain = {}
            )
        }
    )
}