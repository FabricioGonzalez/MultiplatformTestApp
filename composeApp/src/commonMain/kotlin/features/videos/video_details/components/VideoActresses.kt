package features.videos.video_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.ActressEntity
import features.actresses.actresses_list.components.ActressCard
import features.videos.video_details.VideoDetailsContracts

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoActresses(
    modifier: Modifier,
    actresses: List<ActressEntity>,
    windowSizeClass: WindowSizeClass,
    handleEvents: (VideoDetailsContracts.Event) -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        actresses.forEach { actress ->
            ActressCard(modifier = Modifier.size(160.dp), actress = actress, setEvent = {
                handleEvents(
                    VideoDetailsContracts.Event.OnNavigateToActressPressed(
                        it
                    )
                )
            })
        }
    }
}