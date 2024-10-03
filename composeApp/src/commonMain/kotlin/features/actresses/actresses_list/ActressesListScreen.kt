package features.actresses.actresses_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import features.actresses.actresses_list.components.ActressesList
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import presentation.navigation.AppScreenDestinations
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.state.Loading
import presentation.ui.common.state.ManagementResourceUiState
import pro.respawn.flowmvi.api.IntentReceiver
import pro.respawn.flowmvi.compose.dsl.subscribe

data class ActressesListScreen(
    override val route: String = "ActressesList",
    private val navController: NavHostController,
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen, KoinComponent {
    override val key: ScreenKey = "ActressesList"

    private val container = get<ActressListContainer>()

    @Composable
    override fun Content() = with(container.store) {
        val snackbarHostState =
            remember { SnackbarHostState() }/* val (state, setEvent, effect) = use(getScreenModel<ActressesListViewModel>())*/


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
                    navigationIcon = null,
                    searchBar = null,
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    },
                )
            )
        }/* subscribe to store based on system lifecycle - on any platform*/

        val state by subscribe { action ->
            when (action) {
                is ActressListContainer.ActressListAction.ShowMessage -> {}
                is ActressListContainer.ActressListAction.NavigateToDetails -> {
                    navController.navigate(
                        AppScreenDestinations.ActressDetails(actressId = action.actressId)
                    )
                }

                ActressListContainer.ActressListAction.NavigateBack -> navController.navigateUp()
            }
        }

        ActressListContent(state)


        /*effect.collectLatest { effect ->
            when (effect) {
                ActressesListContracts.Effect.CharacterAdded -> snackbarHostState.showSnackbar("Character added to favorites")

                ActressesListContracts.Effect.CharacterRemoved -> snackbarHostState.showSnackbar(
                    "Character removed from favorites"
                )

                ActressesListContracts.Effect.BackNavigation -> navController.navigateUp()
                is ActressesListContracts.Effect.ActressDetailNavigation -> navigator.navigateToActressesDetails(
                    actressId = effect.actressId, onCompose = onCompose
                )
            }
        }*//* Column {
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
             )*/

        /*if (state.searchText.isNotEmpty()) {
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
        }
        else ManagementResourceUiState(
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
    }*/

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntentReceiver<ActressListContainer.ActressListIntent>.ActressListContent(state: ActressListContainer.ActressListState) {
    Column {
        val (searchText, onSearchTextUpdate) = remember {
            mutableStateOf("")
        }

        DockedSearchBar(modifier = Modifier.fillMaxWidth(),
            onExpandedChange = {},
            expanded = false,
            inputField = {
                SearchBarDefaults.InputField(modifier = Modifier.fillMaxWidth(),
                    query = searchText,
                    onExpandedChange = {},
                    expanded = false,
                    trailingIcon = { Icon(Icons.Rounded.Cancel, null) },
                    onQueryChange = {
                        intent(ActressListContainer.ActressListIntent.OnSearchTextChanged(it))
                        onSearchTextUpdate(it)
                    },
                    onSearch = {
                        intent(
                            ActressListContainer.ActressListIntent.OnSearchTextChanged(it)
                        )
                        onSearchTextUpdate(it)
                    })
            },
            content = {})

        when (state) {
            is ActressListContainer.ActressListState.DisplayList -> {
                ManagementResourceUiState(
                    modifier = Modifier.fillMaxSize(),
                    resourceUiState = state.actresses,
                    successView = { actresses ->
                        ActressesList(
                            actresses.collectAsLazyPagingItems(),
                            windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
                        )
                    },
                    onTryAgain = { },
                    onCheckAgain = { },
                )
            }

            is ActressListContainer.ActressListState.DisplaySearchList -> {
                ManagementResourceUiState(
                    modifier = Modifier.fillMaxSize(),
                    resourceUiState = state.searchActresses,
                    successView = { actresses ->
                        ActressesList(
                            actresses.collectAsLazyPagingItems(),
                            windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
                        )
                    },
                    onTryAgain = { },
                    onCheckAgain = { },
                )
            }

            is ActressListContainer.ActressListState.Error -> {}
            ActressListContainer.ActressListState.Loading -> {
                Box {
                    Loading()
                }
            }
        }
    }
}


/*
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
                currentWindowAdaptiveInfo().windowSizeClass,

                )
        }
    }
}*/
