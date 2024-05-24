package features.videos.video_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.rememberImagePainter
import daniel.avila.rnm.kmm.presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.ui.common.ActionBarIcon
import daniel.avila.rnm.kmm.presentation.ui.common.ArrowBackIcon
import daniel.avila.rnm.kmm.presentation.ui.common.state.ManagementResourceUiState
import data.entities.DbVideo
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.parameter.parametersOf

class VideoDetailScreen(
    private val videoId: String,
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val videoDetailViewModel =
            getScreenModel<VideoDetailsViewModel> { parametersOf(videoId) }

        val state by videoDetailViewModel.uiState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
            videoDetailViewModel.effect.collectLatest { effect ->
                when (effect) {
                    VideoDetailsContracts.Effect.CharacterAdded ->
                        snackbarHostState.showSnackbar("Character added to favorites")

                    VideoDetailsContracts.Effect.CharacterRemoved ->
                        snackbarHostState.showSnackbar("Character removed from favorites")

                    VideoDetailsContracts.Effect.BackNavigation -> navigator.pop()
                }
            }
        }

        Scaffold(
            topBar = {
                ActionBar(
                    character = state.video,
                    favorite = state.isFavorite,
                    onActionFavorite = { videoDetailViewModel.setEvent(VideoDetailsContracts.Event.OnFavoriteClick) },
                    onBackPressed = { videoDetailViewModel.setEvent(VideoDetailsContracts.Event.OnBackPressed) }
                )
            }, snackbarHost = {
                SnackbarHost(snackbarHostState, snackbar = { data ->
                    Snackbar(data)
                })
            }
        ) { padding ->
            ManagementResourceUiState(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                resourceUiState = state.video,
                successView = { video ->
                    CharacterDetail(video)
                },
                onTryAgain = { videoDetailViewModel.setEvent(VideoDetailsContracts.Event.OnTryCheckAgainClick) },
                onCheckAgain = { videoDetailViewModel.setEvent(VideoDetailsContracts.Event.OnTryCheckAgainClick) },
            )
        }
    }
}

@Composable
fun CharacterDetail(video: DbVideo) {
    /*val status = video.status*/
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.size(10.dp))
        video.photo?.let { url ->
            Image(
                modifier = Modifier.size(200.dp),
                painter = rememberImagePainter(url),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        /* Text(
             text = "${character.origin}, ${character.species}",
             style = MaterialTheme.typography.h6
         )
         Spacer(modifier = Modifier.size(10.dp))*/
        /*Text(
            text = status.name, color = when (status) {
                Status.ALIVE -> Color.Green
                Status.DEAD -> Color.Red
                Status.UNKNOWN -> Color.Gray
            },
            fontWeight = FontWeight.Bold
        )*/
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBar(
    character: ResourceUiState<DbVideo>,
    favorite: ResourceUiState<Boolean>,
    onActionFavorite: () -> Unit,
    onBackPressed: () -> Unit,
) {
    TopAppBar(
        title = {
            ManagementResourceUiState(
                resourceUiState = character,
                successView = { Text(text = it.title) },
                loadingView = { Text(text = "....") },
                onCheckAgain = {},
                onTryAgain = {}
            )
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