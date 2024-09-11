package features.settings.app_settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import data.ApolloConnector
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.getKoin
import presentation.model.ResourceUiState
import presentation.mvi.use
import presentation.ui.common.AppBarState
import presentation.ui.common.AppTab
import presentation.ui.common.state.ManagementResourceUiState


class AppSettingsScreen(
    override val route: String = "appSettings", override val onCompose: (AppBarState) -> Unit
) : AppTab {
    override val key: ScreenKey = "appSettings"

    override val options: TabOptions
        @Composable get() {
            val title = "Settings"
            val icon = rememberVectorPainter(Icons.Default.Settings)

            return remember {
                TabOptions(
                    index = 0u, title = title, icon = icon
                )
            }
        }

    @Composable
    @Preview
    override fun Content() {

        val (state, setEvent, effects) = use(getScreenModel<AppSettingsViewModel>())

        val preferences = getKoin().get<DataStore<Preferences>>()
        val apolloConnector = getKoin().get<ApolloConnector>()
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val canShowImages by preferences.data.map {
                            ResourceUiState.Success(
                                it[booleanPreferencesKey("can_show_images")] ?: false
                            )
                        }.collectAsState(ResourceUiState.Loading)
                        Text("Can show Images")

                        ManagementResourceUiState(resourceUiState = canShowImages,
                            successView = { result ->
                                Switch(result, onCheckedChange = {
                                    scope.launch {
                                        preferences.edit {
                                            it[booleanPreferencesKey("can_show_images")] =
                                                result.not()
                                        }
                                    }
                                })
                            },
                            onTryAgain = {},
                            onCheckAgain = {})


                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val apiUrl by preferences.data.map {
                            ResourceUiState.Success(
                                it[stringPreferencesKey("api_url")]
                                    ?: "https://mapappapi.bsite.net/graphql/"
                            )
                        }.collectAsState(ResourceUiState.Loading)
                        Text("Api Url")

                        ManagementResourceUiState(resourceUiState = apiUrl,
                            successView = { result ->
                                OutlinedTextField(value = result, onValueChange = { value ->
                                    scope.launch {
                                        preferences.edit {
                                            it[stringPreferencesKey("api_url")] = value
                                        }
                                    }
                                }, trailingIcon = {
                                    IconButton({
                                        apolloConnector.changeClient(result)
                                    }) {
                                        Icon(Icons.Rounded.Save, null)
                                    }
                                })
                            },
                            onTryAgain = {},
                            onCheckAgain = {})


                    }
                }

            }

        }
    }
}