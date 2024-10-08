package features.web_locals.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import components.ImageBox
import components.SwipeToDeleteContainer
import features.web_locals.list.components.Site
import kotlinx.coroutines.flow.collectLatest
import multiplatform.BrowserPage
import presentation.mvi.use
import presentation.navigation.AppScreenDestinations
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.ArrowBackIcon
import presentation.ui.common.state.ManagementResourceUiState

data class WebLocalsListScreen(
    override val route: String = "WebLocalsList",
    private val navController: NavHostController,
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    override val key: ScreenKey = "WebLocalsList"

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val (state, setEvent, effects) = use(getScreenModel<WebLocalsListViewModel>())

        val sizes = currentWindowAdaptiveInfo()



        LaunchedEffect(key1 = Unit) {
            onCompose(
                AppBarState(
                    title = null,
                    actions = {
                        IconButton(onClick = {
                            setEvent(
                                WebLocalsListContracts.Event.NavigateToWebLocalDetailsRequested(
                                    ""
                                )
                            )
                        }) {
                            Icon(Icons.Rounded.Add, null)
                        }
                    },
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

            setEvent(WebLocalsListContracts.Event.OnLoadDataRequested)

            effects.collectLatest { effect ->
                when (effect) {

                    WebLocalsListContracts.Effect.BackNavigation -> navController.navigateUp()
                    is WebLocalsListContracts.Effect.NavigateToWebLocalRequested -> {/*navigator.navigateToActressesDetails(
                            actressId = effect.id,
                            onCompose = onCompose
                        )*/
                    }

                    is WebLocalsListContracts.Effect.NavigateToWebLocalDetailsRequested -> {
                        navController.navigate(
                            AppScreenDestinations.WeblocalDetails(
                                webLocalId = effect.id
                            )
                        )
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

    ElevatedCard(modifier = modifier.padding(8.dp).height(48.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 24.dp, pressedElevation = 18.dp
        ),
        onClick = {
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
                text = site.name,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)
            )
        }
    }
}
