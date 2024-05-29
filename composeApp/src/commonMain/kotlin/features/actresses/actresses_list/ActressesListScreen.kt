package features.actresses.actresses_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.rememberImagePainter
import daniel.avila.rnm.kmm.presentation.ui.common.ArrowBackIcon
import domain.model.ActressEntity
import presentation.ui.common.state.ManagementResourceUiState
import domain.model.VideoDetailsEntity
import kotlinx.coroutines.flow.collectLatest
import presentation.ui.common.AppScreen

data class ActressesListScreen(
    override val route: String = "ActressesList",
) : AppScreen {
    override val key: ScreenKey = "ActressesList"

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val screenModel =
            getScreenModel<ActressesListViewModel>()
        val state by screenModel.uiState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(key1 = Unit) {
            screenModel.effect.collectLatest { effect ->
                when (effect) {
                    ActressesListContracts.Effect.CharacterAdded ->
                        snackbarHostState.showSnackbar("Character added to favorites")

                    ActressesListContracts.Effect.CharacterRemoved ->
                        snackbarHostState.showSnackbar("Character removed from favorites")

                    ActressesListContracts.Effect.BackNavigation -> navigator.pop()
                }
            }
        }
        ManagementResourceUiState(
            modifier = Modifier
                .fillMaxSize(),
            resourceUiState = state.actresses,
            successView = { actresses ->
                ActressesList(actresses.collectAsLazyPagingItems(), setEvent = screenModel::setEvent)
            },
            onTryAgain = { },
            onCheckAgain = { },
        )

    }

    @Composable
    fun ActressesList(actresses: LazyPagingItems<ActressEntity>, setEvent: Any) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(actresses.itemCount) { entity ->
                actresses[entity]?.let { actress ->
                    Card(Modifier.height(120.dp).width(120.dp)) {
                        Box {
                            Image(
                                painter = rememberImagePainter(actress.photo),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                                    .background(MaterialTheme.colorScheme.background.copy(0.4f)), text = actress.name
                            )
                        }
                    }
                }
            }
        }
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
