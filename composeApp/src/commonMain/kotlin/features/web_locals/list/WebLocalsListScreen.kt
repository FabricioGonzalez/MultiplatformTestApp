package features.web_locals.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.ImageBox
import components.SwipeToDeleteContainer
import features.navigation.navigateToWebLocalDetails
import features.web_locals.list.components.Site
import kotlinx.coroutines.flow.collectLatest
import multiplatform.BrowserPage
import presentation.mvi.use
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.ManagementResourceUiState

data class WebLocalsListScreen(
    override val route: String = "WebLocalsList",
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    override val key: ScreenKey = "WebLocalsList"

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val (state, setEvent, effects) = use(getScreenModel<WebLocalsListViewModel>())

        val sizes = calculateWindowSizeClass()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
            onCompose(
                AppBarState(
                    title = null,
                    actions = {
                        IconButton(onClick = {
                            setEvent(WebLocalsListContracts.Event.NavigateToWebLocalDetailsRequested(""))
                        }) {
                            Icon(Icons.Rounded.Add, null)
                        }
                    },
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

            setEvent(WebLocalsListContracts.Event.OnLoadDataRequested)

            effects.collectLatest { effect ->
                when (effect) {

                    WebLocalsListContracts.Effect.BackNavigation -> navigator.pop()
                    is WebLocalsListContracts.Effect.NavigateToWebLocalRequested -> {/*navigator.navigateToActressesDetails(
                            actressId = effect.id,
                            onCompose = onCompose
                        )*/
                    }

                    is WebLocalsListContracts.Effect.NavigateToWebLocalDetailsRequested -> {
                        navigator.navigateToWebLocalDetails(webLocalId = effect.id, onCompose = onCompose)
                    }
                }
            }
        }
        ManagementResourceUiState(
            modifier = Modifier.fillMaxSize(),
            resourceUiState = state.sites,
            successView = { sites ->
                LazyColumn(
                    modifier = Modifier.padding(4.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sites, key = { it.id ?: it.url }) { site ->
                        SwipeToDeleteContainer(modifier = Modifier, item = site, onDelete = {
                            setEvent(WebLocalsListContracts.Event.OnDeleteRequested(site.id ?: ""))
                        }, onEdit = {
                            setEvent(
                                WebLocalsListContracts.Event.NavigateToWebLocalDetailsRequested(
                                    it.id ?: ""
                                )
                            )
                        }) {
                            SiteCard(site = site)
                        }

                    }
                }
            })


    }
}


@Composable
fun SiteCard(modifier: Modifier = Modifier, site: Site) {
    val (showPage, setShowPage) = remember {
        mutableStateOf(false)
    }
    if (showPage) BrowserPage(site.url, onBrowserClosed = { setShowPage(false) })

    ElevatedCard(modifier = modifier.padding(8.dp).height(48.dp), elevation = CardDefaults.elevatedCardElevation(
        defaultElevation = 24.dp, pressedElevation = 18.dp
    ), onClick = {
        setShowPage(true)
    }) {
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(48.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.Start)
        ) {
            ImageBox(
                modifier = Modifier.size(32.dp), photo = ""/* Uri.withAppendedPath(Uri.parse(site.url), "favicon.ico")
                    .toString()*/
            )
            Text(
                text = site.name, modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)
            )
        }
    }
}
