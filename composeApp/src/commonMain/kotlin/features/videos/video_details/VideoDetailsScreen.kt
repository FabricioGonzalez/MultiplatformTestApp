package features.videos.video_details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import daniel.avila.rnm.kmm.presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.ManagementResourceUiState
import domain.model.VideoDetailsEntity
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.parameter.parametersOf
import presentation.ui.common.AppScreen

data class VideoDetailScreen(
    private val videoId: String, override val route: String = "VideoDetails",
) : AppScreen {
    override val key: ScreenKey = "VideoDetails"

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
        ManagementResourceUiState(
            modifier = Modifier
                .fillMaxSize(),
            resourceUiState = state.video,
            successView = { video ->
                CharacterDetail(video)
            },
            onTryAgain = { },
            onCheckAgain = { },
        )

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun TopBarContent() {
        val navigation = LocalNavigator.current

        TopAppBar(
            title = {},
            navigationIcon = {
                ArrowBackIcon {
                    navigation?.pop()
                }
            },
        )
    }

}

@Composable
fun CharacterDetail(video: VideoDetailsEntity) {
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
        /*Image(
            modifier = Modifier.size(200.dp),
            painter = rememberImagePainter(video.photo),
            contentDescription = null,
        )*/
        Spacer(modifier = Modifier.size(10.dp))
    }
}
