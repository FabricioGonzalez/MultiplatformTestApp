package features.videos.video_details.components

import BrowserPage
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.PlayerEntity


@Composable
fun PlayerItem(
    modifier: Modifier = Modifier,
    player: PlayerEntity,
    videoId: String,
    setEvent: (String) -> Unit
) {
    var isWorking by remember {
        mutableStateOf(player.isWorking)
    }
    var showPage by remember {
        mutableStateOf(false)
    }
    if (showPage) BrowserPage(player.playerLink, onBrowserClosed = { showPage = false })

    Card(modifier = modifier, onClick = {
        setEvent(videoId)
        showPage = true
    }) {
        Icon(
            imageVector = if (isWorking) Icons.Filled.CheckCircle else Icons.Filled.Delete,
            contentDescription = null,
            tint = if (isWorking) Color.Green.copy(
                0.8f, 0.1f, 0.7f, 0.2f
            ) else Color.Red.copy(
                0.8f, 0.8f, 0.1f, 0.2f
            )
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow, contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = player.playerLink, style = MaterialTheme.typography.bodySmall
            )
        }
    }
}