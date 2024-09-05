package features.actresses.actress_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import components.ImageBox
import domain.model.ActressEntity
import features.actresses.actress_details.ActressDetailsContracts


@Composable
fun ActressDetailsHeader(
    modifier: Modifier = Modifier,
    actress: ActressEntity,
    isEditing: Boolean,
    windowSizeClass: WindowSizeClass,
    setEvent: (ActressDetailsContracts.Event) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ImageBox(modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth(), photo = actress.photo)

        Spacer(modifier = Modifier.size(10.dp))
        if (isEditing) {
            TextField(
                value = actress.name,
                onValueChange = { setEvent(ActressDetailsContracts.Event.OnNameChanged(it)) },
                readOnly = false
            )

            TextField(
                value = actress.photo,
                onValueChange = { setEvent(ActressDetailsContracts.Event.OnPhotoChanged(it)) },
                readOnly = false
            )
        } else {
            Text(
                text = actress.name,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}