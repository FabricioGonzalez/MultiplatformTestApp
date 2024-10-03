package features.videos.video_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import data.helpers.format
import features.videos.video_details.components.Players
import features.videos.video_details.components.Tags
import features.videos.video_details.components.VideoActresses
import features.videos.video_details.components.VideoDetailsHeader
import org.koin.compose.koinInject
import presentation.navigation.AppScreenDestinations
import presentation.ui.common.AppBarState
import presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.Loading
import pro.respawn.flowmvi.api.IntentReceiver
import pro.respawn.flowmvi.compose.dsl.subscribe

@Composable
fun VideoDetailScreen(
    videoId: String,
    navController: NavHostController,
    onCompose: (AppBarState) -> Unit,
) = with(koinInject<VideoDetailsStore>().store) {
    val snackbarHostState = remember { SnackbarHostState() }

    val sizes = currentWindowAdaptiveInfo().windowSizeClass

    val storeScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        start(storeScope)
        onDispose {
            close()
        }
    }

    LaunchedEffect(key1 = Unit) {
        onCompose(
            AppBarState(
                title = null,
                actions = null,
                navigationIcon = {
                    ArrowBackIcon {
                        navController.navigateUp()
                    }
                },
                searchBar = null,
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                },
            )
        )

        intent(VideoDetailsIntents.OnLoadDataRequested(videoId = videoId))

    }

    val state by subscribe { action ->
        when (action) {
            VideoDetailsEffect.CharacterAdded -> snackbarHostState.showSnackbar("Character added to favorites")

            VideoDetailsEffect.CharacterRemoved -> snackbarHostState.showSnackbar(
                "Character removed from favorites"
            )

            VideoDetailsEffect.BackNavigation -> navController.navigateUp()
            is VideoDetailsEffect.NavigateToActressesRequested -> {
                navController.navigate(
                    AppScreenDestinations.ActressDetails(
                        actressId = action.id
                    )
                )
            }

            is VideoDetailsEffect.PlayVideoRequested -> {}
        }
    }

    VideoDetailsPage(
        state = state, windowClassSizes = sizes
    )


}

@Composable
internal fun IntentReceiver<VideoDetailsIntents>.VideoDetailsPage(
    state: VideoDetailsState,
    windowClassSizes: WindowSizeClass,
) {
    when (state) {
        VideoDetailsState.Empty -> {
            Text("Vish, está vázio?")
        }

        is VideoDetailsState.Error -> presentation.ui.common.state.Error(
            onTryAgain = {},
            msg = state.message
        )

        VideoDetailsState.Loading -> Loading()
        is VideoDetailsState.Success -> {
            Column(
                Modifier.fillMaxSize().padding(8.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VideoDetailsHeader(modifier = Modifier.fillMaxWidth(), video = state.video)

                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.EditCalendar, null)
                        Text(state.video.createdAt.format())
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.EditCalendar, null)
                        Text(state.video.addedToAt?.format() ?: "Sem data")
                    }
                }

                Players(modifier = Modifier.fillMaxWidth(),
                    players = state.video.players,
                    videoId = state.video.id,
                    setEvent = { id ->
                        intent(VideoDetailsIntents.OnPlayVideoPressed(id))
                    })

                VideoActresses(
                    modifier = Modifier.fillMaxWidth(),
                    actresses = state.video.actresses,
                    windowClassSizes
                )
                Tags(
                    modifier = Modifier.fillMaxWidth(), tags = state.video.tags
                )
            }
        }
    }

}

/*@Preview
@Composable
private fun VideoDetailsPagePreview() {
    MaterialTheme {
        Surface {
            VideoDetailsPage(VideoDetailsContracts.State(
                ResourceUiState.Success(
                    VideoDetailsEntity(
                        id = "",
                        title = "",
                        photo = "",
                        createdAt = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()),
                        addedToAt = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()),
                        actresses = listOf(),
                        tags = listOf(),
                        players = listOf(),
                    )
                ), ResourceUiState.Empty
            ), currentWindowAdaptiveInfo().windowSizeClass, setEvent = {})
        }
    }
}*/
