package features.settings.app_settings.ui

import androidx.paging.PagingData
import domain.model.VideoEntity
import kotlinx.coroutines.flow.Flow
import presentation.model.ResourceUiState
import presentation.mvi.UiEffect
import presentation.mvi.UiEvent
import presentation.mvi.UiState

interface AppSettingsContract {
    sealed interface Event : UiEvent {
        data object OnTryCheckAgainClick : Event
        data object OnLoadDataRequested : Event
        data class OnVideoItemClicked(val itemId: String) : Event
        data class OnSearchTextChanged(val searchText: String) : Event
        data object OnBackPressed : Event
    }

    data class State(
        val searchFeed: ResourceUiState<Flow<PagingData<VideoEntity>>>,
        val searchText: String,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data object BackNavigation : Effect
        data class NavigateToDetails(val id: String) : Effect
    }
}
