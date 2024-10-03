package features.actresses.actress_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import features.actresses.actress_details.components.ActressDetailsHeader
import features.home.components.VideosList
import kotlinx.coroutines.flow.collectLatest
import presentation.model.ResourceUiState
import presentation.mvi.use
import presentation.navigation.AppScreenDestinations
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.ManagementResourceUiState

data class ActressDetailsScreen(
    private val actressId: String, override val route: String = "ActressDetails",
    private val navController: NavHostController,
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    override val key: ScreenKey = "ActressDetails"

    @Composable
    override fun Content() {
        val sizes = currentWindowAdaptiveInfo().windowSizeClass
        val snackbarHostState = remember { SnackbarHostState() }
        val (state, setEvent, effect) = use(getScreenModel<ActressDetailsViewModel>())



        LaunchedEffect(key1 = Unit) {
            setEvent(ActressDetailsContracts.Event.OnLoadDataRequested(actressId))

            effect.collectLatest { effect ->
                when (effect) {
                    ActressDetailsContracts.Effect.CharacterAdded -> snackbarHostState.showSnackbar(
                        "Character added to favorites"
                    )

                    is ActressDetailsContracts.Effect.OnActressPhotoRequested -> navController.navigate(
                        AppScreenDestinations.ActressPictureSearch(
                            effect.actressName,
                        )
                    )

                    ActressDetailsContracts.Effect.CharacterRemoved -> snackbarHostState.showSnackbar(
                        "Character removed from favorites"
                    )

                    is ActressDetailsContracts.Effect.OnVideoItemClicked -> {
                        navController.navigate(
                            AppScreenDestinations.VideoDetails(videoId = effect.videoId)
                        )
                    }

                    ActressDetailsContracts.Effect.BackNavigation -> navController.navigateUp()
                }
            }
        }

        Column(Modifier.fillMaxSize()) {
            LaunchedEffect(key1 = state.actress, key2 = state.isFavorite) {
                onCompose(
                    AppBarState(
                        title = null,
                        actions = {
                            IconButton(onClick = {
                                setEvent(
                                    ActressDetailsContracts.Event.OnActressFavorited(
                                        !state.isFavorite
                                    )
                                )
                            }) {
                                Icon(
                                    if (state.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    null
                                )
                            }

                            IconButton(onClick = {
                                if (state.actress is ResourceUiState.Success)
                                    setEvent(
                                        ActressDetailsContracts.Event.OnActressPhotoRequested(
                                            state.actress.data.name
                                        )
                                    )
                            }) {
                                Icon(Icons.Rounded.PhotoLibrary, null)
                            }
                            IconButton(onClick = {
                                setEvent(
                                    ActressDetailsContracts.Event.OnEditRequested(
                                        !state.isEditing
                                    )
                                )
                            }) {
                                Icon(
                                    if (state.isEditing) Icons.Rounded.SaveAlt else Icons.Rounded.Edit,
                                    null
                                )
                            }
                        },
                        navigationIcon = {
                            ArrowBackIcon {
                                navController.navigateUp()
                            }
                        },
                        searchBar = null,
                        snackbarHost = null,
                    )
                )
            }
            ManagementResourceUiState(
                modifier = Modifier.fillMaxWidth(),
                resourceUiState = state.actress,
                successView = { actress ->
                    Column {
                        ActressDetailsHeader(
                            actress = actress,
                            windowSizeClass = sizes,
                            isEditing = state.isEditing,
                            setEvent = setEvent
                        )
                    }
                },
                onTryAgain = { },
                onCheckAgain = { },
            )
            ManagementResourceUiState(
                modifier = Modifier.fillMaxWidth(),
                resourceUiState = state.videos,
                successView = { videos ->
                    VideosList(
                        videos.collectAsLazyPagingItems(),
                        windowSizeClass = sizes,
                        setEvent = setEvent
                    )
                },
                onTryAgain = { },
                onCheckAgain = { },
            )
        }


    }

}

