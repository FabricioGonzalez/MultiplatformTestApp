package features.actresses.actresses_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import app.cash.paging.compose.LazyPagingItems
import components.ImageBox
import domain.model.ActressEntity
import features.actresses.actresses_list.ActressListContainer
import features.home.components.rememberColumns
import pro.respawn.flowmvi.api.IntentReceiver


@Composable
fun IntentReceiver<ActressListContainer.ActressListIntent>.ActressesList(
    actresses: LazyPagingItems<ActressEntity>,
    windowSizeClass: WindowSizeClass,
) {

    LazyVerticalGrid(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        columns = rememberColumns(windowSizeClass),
        modifier = Modifier.padding(8.dp)
    ) {
        items(actresses.itemCount) { entity ->
            actresses[entity]?.let { actress ->
                ActressCard(modifier = Modifier.height(220.dp).width(120.dp),
                    actress = actress,
                    setEvent = { id ->
                        intent(
                            ActressListContainer.ActressListIntent.OnNavigateToActressDetailRequested(
                                id
                            )
                        )
                    })
            }
        }
    }
}

@Composable
fun ActressCard(
    modifier: Modifier = Modifier,
    actress: ActressEntity,
    setEvent: (actressId: String) -> Unit,
) {
    Card(modifier = modifier, onClick = {
        setEvent(actress.id)

    }) {
        Box {
            ImageBox(
                modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium),
                photo = actress.photo
            )
            Text(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.background.copy(0.4f)).padding(4.dp),
                textAlign = TextAlign.Center,
                text = actress.name
            )
        }
    }
}