package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.flow.map
import org.koin.compose.getKoin
import presentation.model.ResourceUiState
import presentation.ui.common.state.ManagementResourceUiState


@Composable
fun ImageBox(modifier: Modifier = Modifier, photo: String) {
    val preferences = getKoin().get<DataStore<Preferences>>()
    val canShowImages by preferences.data.map {
        ResourceUiState.Success(
            it[booleanPreferencesKey("can_show_images")] ?: false
        )
    }.collectAsState(ResourceUiState.Loading)

    ManagementResourceUiState(
        resourceUiState = canShowImages,
        successView = { result ->
            Image(
                modifier = modifier.then(
                    if (!result) Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    else Modifier
                ),
                painter = if (result) rememberImagePainter(photo) else rememberVectorPainter(
                    Icons.Rounded.Cancel
                ),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        },
        onTryAgain = {},
        onCheckAgain = {})


}