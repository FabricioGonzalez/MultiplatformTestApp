package features.actresses.actresses_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.model.ActressEntity
import features.actresses.actress_details.ActressDetailsScreen
import features.actresses.actresses_list.components.ActressesList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.ManagementResourceUiState

data class ActressesListScreen(
    override val route: String = "ActressesList",
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    override val key: ScreenKey = "ActressesList"

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val sizes = calculateWindowSizeClass()

        val snackbarHostState = remember { SnackbarHostState() }
        val screenModel =
            getScreenModel<ActressesListViewModel>()
        val state by screenModel.uiState.collectAsState()

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
                    snackbarHost = null,
                )
            )

            screenModel.effect.collectLatest { effect ->
                when (effect) {
                    ActressesListContracts.Effect.CharacterAdded ->
                        snackbarHostState.showSnackbar("Character added to favorites")

                    ActressesListContracts.Effect.CharacterRemoved ->
                        snackbarHostState.showSnackbar("Character removed from favorites")

                    ActressesListContracts.Effect.BackNavigation -> navigator.pop()
                    is ActressesListContracts.Effect.ActressDetailNavigation -> navigator.push(
                        ActressDetailsScreen(
                            actressId = effect.actressId, onCompose = onCompose
                        )
                    )
                }
            }
        }
        ManagementResourceUiState(
            modifier = Modifier
                .fillMaxSize(),
            resourceUiState = state.actresses,
            successView = { actresses ->
                ActressesList(
                    actresses.collectAsLazyPagingItems(),
                    setEvent = screenModel::setEvent,
                    windowSizeClass = sizes
                )
            },
            onTryAgain = { },
            onCheckAgain = { },
        )

    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun PreviewActressList() {
    MaterialTheme {
        Surface {
            ActressesList(
                actresses = flow {
                    emit(
                        PagingData.from(
                            listOf(
                                ActressEntity(
                                    id = "",
                                    name = "Ayala Test",
                                    photo = "",
                                    link = "",
                                    isFavorite = false
                                ),
                                ActressEntity(
                                    id = "",
                                    name = "Ayala Test",
                                    photo = "",
                                    link = "",
                                    isFavorite = false
                                ), ActressEntity(
                                    id = "",
                                    name = "Ayala Test",
                                    photo = "",
                                    link = "",
                                    isFavorite = false
                                ), ActressEntity(
                                    id = "",
                                    name = "Ayala Test",
                                    photo = "",
                                    link = "",
                                    isFavorite = false
                                ), ActressEntity(
                                    id = "",
                                    name = "Ayala Test",
                                    photo = "",
                                    link = "",
                                    isFavorite = false
                                ), ActressEntity(
                                    id = "",
                                    name = "Ayala Test",
                                    photo = "",
                                    link = "",
                                    isFavorite = false
                                ), ActressEntity(
                                    id = "",
                                    name = "Ayala Test",
                                    photo = "",
                                    link = "",
                                    isFavorite = false
                                )
                            )
                        )
                    )
                }.collectAsLazyPagingItems(),
                setEvent = {},
                calculateWindowSizeClass(),

                )
        }
    }
}