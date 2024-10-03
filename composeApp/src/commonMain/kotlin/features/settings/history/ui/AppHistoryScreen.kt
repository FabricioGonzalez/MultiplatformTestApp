package features.settings.history.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.material3.DataTable
import com.seanproctor.datatable.rememberDataTableState
import components.ImageBox
import data.helpers.format
import kotlinx.coroutines.flow.collectLatest
import presentation.mvi.use
import presentation.navigation.AppScreenDestinations
import presentation.ui.common.AppBarState
import presentation.ui.common.AppTab
import presentation.ui.common.state.ManagementResourceUiState

class AppHistoryScreen(
    override val route: String = "history",
    private val navController: NavHostController,
    override val onCompose: (AppBarState) -> Unit,
) : AppTab {

    override val key: ScreenKey = "History"

    override val options: TabOptions
        @Composable get() {
            val title = "History"
            val icon = rememberVectorPainter(Icons.Default.History)

            return remember {
                TabOptions(
                    index = 1u, title = title, icon = icon
                )
            }
        }


    @Composable
    override fun Content() {
        val (state, setEvent, effect) = use(getScreenModel<AppHistoryViewModel>())/*val sizes = currentWindowAdaptiveInfo()*/

        val snackbarHostState = remember { SnackbarHostState() }



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
            setEvent(AppHistoryContract.Event.OnLoadDataRequested)

            effect.collectLatest { effect ->
                when (effect) {
                    AppHistoryContract.Effect.CharacterAdded -> snackbarHostState.showSnackbar("Character added to favorites")

                    AppHistoryContract.Effect.CharacterRemoved -> snackbarHostState.showSnackbar("Character removed from favorites")

                    AppHistoryContract.Effect.BackNavigation -> {
                        navController.navigateUp()
                    }

                    is AppHistoryContract.Effect.NavigateToDetails -> {
                        navController.navigate(
                            AppScreenDestinations.VideoDetails(
                                videoId = effect.id
                            )
                        )
                    }

                }
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            ManagementResourceUiState(modifier = Modifier.fillMaxSize(),
                resourceUiState = state.historyEntries,
                successView = { histories ->

                    DataTable(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.medium)
                            .horizontalScroll(rememberScrollState()),
                        state = rememberDataTableState(),
                        background = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                        columns = listOf(
                            DataColumn(
                                width = TableColumnWidth.Fixed(126.dp),
                                alignment = Alignment.Center
                            ) {
                                Text("Image")
                            },
                            DataColumn(
                                width = TableColumnWidth.MaxIntrinsic,
                                alignment = Alignment.Center
                            ) {
                                Text("Title", minLines = 2)
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(144.dp),
                                alignment = Alignment.Center
                            ) {
                                Text("WatchedOn")
                            },
                        ),

                        rowHeight = 96.dp
                    ) {
                        histories.forEach {
                            row {

                                onClick = {
                                    setEvent(
                                        AppHistoryContract.Event.OnGoToVideoDetailsRequested(
                                            it.id
                                        )
                                    )
                                }
                                cell {
                                    it.image?.let { image ->
                                        ImageBox(
                                            modifier = Modifier.width(126.dp).height(92.dp)
                                                .clip(MaterialTheme.shapes.medium), photo = image
                                        )
                                    }
                                }
                                cell { Text(it.videoTitle) }

                                cell { Text(it.watchedOn.format()) }
                            }
                        }
                    }
                },
                onCheckAgain = {},
                onTryAgain = {})
        }
    }
}