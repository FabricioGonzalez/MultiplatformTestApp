package features.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import domain.model.VideoEntity

@Composable
fun VideoDetails(modifier: Modifier = Modifier, video: VideoEntity) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(36.dp))
    ) {

        Image(
            painter = rememberImagePainter(video.photo, filterQuality = FilterQuality.High),
            contentScale = ContentScale.FillBounds,
            contentDescription = "image",
        )

        Text(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(0.4f))
                .align(Alignment.BottomCenter),
            text = video.title,
            style = MaterialTheme.typography.titleSmall
        )
    }
}