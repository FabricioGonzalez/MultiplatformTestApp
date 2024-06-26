package features.videos.video_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.PlayerEntity


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Players(modifier: Modifier, players: List<PlayerEntity>) {
    FlowRow(
        modifier = modifier, horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp, alignment = Alignment.CenterHorizontally
        ), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        players.forEach { player ->
            PlayerItem(
                modifier = Modifier.width(164.dp).height(64.dp), player = player
            )
        }
    }
}