package features.videos.video_details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Discount
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.TagEntity
import features.videos.video_details.VideoDetailsContracts

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Tags(
    modifier: Modifier = Modifier,
    tags: List<TagEntity>,
    setEvent: (VideoDetailsContracts.Event) -> Unit
) {
    FlowRow(
        modifier = modifier,
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        tags.forEach { tag ->
            val (preferred, setPreferred) = mutableStateOf(tag.isFavorite)

            AssistChip(modifier = Modifier.height(24.dp), onClick = {
                setEvent(VideoDetailsContracts.Event.OnTagFavoritedChanged(tag.name))
                setPreferred(!preferred)
            }, colors = AssistChipDefaults.assistChipColors(
                containerColor = if (preferred) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                labelColor = if (preferred) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
                leadingIconContentColor = if (preferred) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
            ), border = BorderStroke(
                0.3.dp, if (preferred) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.onSurface
            ),

                label = {
                    Text(
                        text = tag.name, style = TextStyle.Default.copy(
                            fontSize = 10.sp
                        )
                    )
                }, leadingIcon = {
                    Icon(
                        modifier = Modifier.size(10.dp),
                        imageVector = Icons.Rounded.Discount,
                        contentDescription = tag.name
                    )
                })
        }
    }
}