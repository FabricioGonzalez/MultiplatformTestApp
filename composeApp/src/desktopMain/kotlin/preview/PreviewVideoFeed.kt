package preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import domain.model.VideoEntity
import features.home.components.VideosListFeed
import features.home.data.VideoFeed
import kotlinx.coroutines.flow.flowOf
import presentation.model.ResourceUiState

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview()
@Composable
fun PreviewFeed() {

    Surface {
        val size = calculateWindowSizeClass()

        VideosListFeed(windowSizeClass = size, videos = listOf(
            VideoFeed(
                title = "Recent", videos = ResourceUiState.Success(
                    flowOf(
                        PagingData.from(
                            listOf(
                                VideoEntity(
                                    id = "1", title = "Teste", photo = ""
                                ), VideoEntity(
                                    id = "2", title = "Teste", photo = ""
                                ), VideoEntity(
                                    id = "3", title = "Teste", photo = ""
                                ), VideoEntity(
                                    id = "4", title = "Teste", photo = ""
                                ), VideoEntity(
                                    id = "5", title = "Teste", photo = ""
                                )
                            )
                        )
                    )
                )
            )
        ), onSweetsSelected = { /*setEvent(HomeContract.Event.OnVideoItemClicked(it.id)) */ })
    }
}
