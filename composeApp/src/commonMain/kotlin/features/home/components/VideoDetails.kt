package features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import components.ImageBox
import domain.model.VideoEntity
import features.actresses.actress_details.ActressDetailsContracts

@Composable
fun VideoDetails(
    modifier: Modifier = Modifier,
    video: VideoEntity,
    windowSizeClass: WindowSizeClass,
    setEvent: (ActressDetailsContracts.Event) -> Unit
) {
    Box(
        modifier = modifier
            .clickable {
                setEvent(ActressDetailsContracts.Event.OnVideoItemClicked(video.id))
            }
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(36.dp))
    ) {

        ImageBox(modifier = Modifier, photo = video.photo)

        Text(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(0.4f))
                .align(Alignment.BottomCenter),
            text = video.title,
            style = MaterialTheme.typography.titleSmall
        )
    }
}