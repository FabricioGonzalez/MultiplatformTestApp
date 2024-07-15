package features.videos.video_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
        AddCard(modifier = Modifier.size(160.dp), title = "Add new Actress", onAddClicked = {

        })
    }
}

@Composable
fun AddCard(modifier: Modifier, title: String, onAddClicked: () -> Unit) {
    Card(modifier = modifier, onClick = {
        onAddClicked()

    }) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Rounded.AddCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp).align(Alignment.Center),
            )
            Text(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.background.copy(0.4f)).padding(4.dp),
                textAlign = TextAlign.Center,
                text = title
            )
        }
    }
}
