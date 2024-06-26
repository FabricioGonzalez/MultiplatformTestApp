package features.videos.video_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.ImageBox
import domain.model.VideoDetailsEntity


@Composable
fun VideoDetailsHeader(modifier: Modifier = Modifier, video: VideoDetailsEntity) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ImageBox(modifier = Modifier, photo = video.photo)
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = video.title,
            style = MaterialTheme.typography.titleSmall
        )
    }
}
