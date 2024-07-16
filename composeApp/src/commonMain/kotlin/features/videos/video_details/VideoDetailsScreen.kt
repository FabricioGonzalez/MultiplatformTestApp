package features.videos.video_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.helpers.format
import domain.model.VideoDetailsEntity
import features.navigation.navigateToActressesDetails
import features.videos.video_details.components.Players
import features.videos.video_details.components.Tags
import features.videos.video_details.components.VideoActresses
import features.videos.video_details.components.VideoDetailsHeader
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.model.ResourceUiState
import presentation.mvi.use
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.ManagementResourceUiState

data class VideoDetailScreen(
    private val videoId: String, override val route: String = "VideoDetails",
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    override val key: ScreenKey = "VideoDetails"

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val (state, setEvent, effects) = use(getScreenModel<VideoDetailsViewModel>())

        val sizes = calculateWindowSizeClass()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
            onCompose(
                AppBarState(
                    title = null,
                    actions = null,
                    navigationIcon = {
                        ArrowBackIcon {
                            navigator.pop()
                        }
                    },
                    searchBar = null,
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    },
                )
            )

            setEvent(VideoDetailsContracts.Event.OnLoadDataRequested(videoId = videoId))

            effects.collectLatest { effect ->
                when (effect) {
                    VideoDetailsContracts.Effect.CharacterAdded -> snackbarHostState.showSnackbar("Character added to favorites")

                    VideoDetailsContracts.Effect.CharacterRemoved -> snackbarHostState.showSnackbar(
                        "Character removed from favorites"
                    )

                    VideoDetailsContracts.Effect.BackNavigation -> navigator.pop()
                    is VideoDetailsContracts.Effect.NavigateToActressesRequested -> {
                        navigator.navigateToActressesDetails(
                            actressId = effect.id,
                            onCompose = onCompose
                        )
                    }

                    is VideoDetailsContracts.Effect.PlayVideoRequested -> {}
                }
            }
        }

        VideoDetailsPage(
            state = state,
            windowClassSizes = sizes,
            setEvent = setEvent
        )
    }
}

@Composable
private fun VideoDetailsPage(
    state: VideoDetailsContracts.State,
    windowClassSizes: WindowSizeClass,
    setEvent: (VideoDetailsContracts.Event) -> Unit
) {
    ManagementResourceUiState(
        modifier = Modifier.fillMaxSize(),
        resourceUiState = state.video,
        successView = { video ->
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VideoDetailsHeader(modifier = Modifier.fillMaxWidth(), video = video)

                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(video.createdAt.format())
                    Text(video.addedToAt?.format() ?: "Sem data")
                }


                Players(
                    modifier = Modifier.fillMaxWidth(), players = video.players,
                    videoId = video.id,
                    setEvent = { id ->
                        setEvent(VideoDetailsContracts.Event.OnPlayVideoPressed(id))
                    }
                )

                VideoActresses(
                    modifier = Modifier.fillMaxWidth(),
                    actresses = video.actresses,
                    windowClassSizes,
                    setEvent,
                )
                Tags(
                    modifier = Modifier.fillMaxWidth(), tags = video.tags, setEvent = setEvent
                )
            }
        },
        onTryAgain = { },
        onCheckAgain = { },
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun VideoDetailsPagePreview() {
    MaterialTheme {
        Surface {
            VideoDetailsPage(
                VideoDetailsContracts.State(
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
                    ),
                    ResourceUiState.Empty
                ),
                calculateWindowSizeClass(),
                setEvent = {}
            )
        }
    }
}
