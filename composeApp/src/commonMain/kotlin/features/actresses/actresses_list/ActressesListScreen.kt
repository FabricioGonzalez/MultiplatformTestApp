package features.actresses.actresses_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.model.ActressEntity
import features.actresses.actresses_list.components.ActressesList
import features.navigation.navigateToActressesDetails
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.mvi.use
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.state.ManagementResourceUiState

data class ActressesListScreen(
    override val route: String = "ActressesList",
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    override val key: ScreenKey = "ActressesList"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val sizes = currentWindowAdaptiveInfo().windowSizeClass

        val snackbarHostState = remember { SnackbarHostState() }
        val (state, setEvent, effect) = use(getScreenModel<ActressesListViewModel>())

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

            effect.collectLatest { effect ->
                when (effect) {
                    ActressesListContracts.Effect.CharacterAdded -> snackbarHostState.showSnackbar("Character added to favorites")

                    ActressesListContracts.Effect.CharacterRemoved -> snackbarHostState.showSnackbar(
                        "Character removed from favorites"
                    )

                    ActressesListContracts.Effect.BackNavigation -> navigator.pop()
                    is ActressesListContracts.Effect.ActressDetailNavigation -> navigator.navigateToActressesDetails(
                        actressId = effect.actressId, onCompose = onCompose
                    )
                }
            }
        }
        Column {
            DockedSearchBar(
                modifier = Modifier.fillMaxWidth().padding(8.dp, 4.dp),
                query = state.searchText,
                onQueryChange = {
                    setEvent(ActressesListContracts.Event.OnSearchTextChanged(it))
                },
                onActiveChange = {},
                onSearch = {
                    setEvent(ActressesListContracts.Event.OnSearchTextChanged(it))
                },
                content = {},
                active = false
            )

            if (state.searchText.isNotEmpty()) {
                ManagementResourceUiState(
                    modifier = Modifier.fillMaxSize(),
                    resourceUiState = state.searchActresses,
                    successView = { actresses ->
                        ActressesList(
                            actresses.collectAsLazyPagingItems(),
                            setEvent = setEvent,
                            windowSizeClass = sizes
                        )
                    },
                    onTryAgain = { },
                    onCheckAgain = { },
                )
            } else ManagementResourceUiState(
                modifier = Modifier.fillMaxSize(),
                resourceUiState = state.actresses,
                successView = { actresses ->
                    ActressesList(
                        actresses.collectAsLazyPagingItems(),
                        setEvent = setEvent,
                        windowSizeClass = sizes
                    )
                },
                onTryAgain = { },
                onCheckAgain = { },
            )
        }

    }
}

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
                currentWindowAdaptiveInfo().windowSizeClass,

                )
        }
    }
}