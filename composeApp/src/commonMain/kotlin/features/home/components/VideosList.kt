package features.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import app.cash.paging.compose.LazyPagingItems
import domain.model.VideoEntity
import features.actresses.actress_details.ActressDetailsContracts


@Composable
fun VideosList(
    videos: LazyPagingItems<VideoEntity>,
    setEvent: (ActressDetailsContracts.Event) -> Unit,
    windowSizeClass: WindowSizeClass
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text("Videos")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            items(videos.itemCount) { entity ->
                videos[entity]?.let { video ->
                    VideoDetails(
                        modifier = Modifier.height(180.dp).width(180.dp),
                        video = video,
                        windowSizeClass = windowSizeClass,
                        setEvent = setEvent
                    )
                }
            }
        }
    }
}