package components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.rememberImagePainter


@Composable
fun ImageBox(modifier: Modifier = Modifier, photo: String) {
    Image(
        modifier = modifier/*.alpha(0f).background(Color.Cyan)*/,
        painter = rememberImagePainter(photo),
        contentDescription = null,
        contentScale = ContentScale.FillBounds
    )
}