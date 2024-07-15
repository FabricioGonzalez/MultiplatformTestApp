package features.settings.history.ui

import androidx.paging.PagingData
import domain.model.HistoryEntry
import domain.model.VideoEntity
import kotlinx.coroutines.flow.Flow
import presentation.model.ResourceUiState
import presentation.mvi.UiEffect
import presentation.mvi.UiEvent
import presentation.mvi.UiState

interface AppHistoryContract {
    sealed interface Event : UiEvent {
        data object OnLoadDataRequested : Event
        data class OnGoToVideoDetailsRequested(val videoId: String) : Event
        data object OnBackPressed : Event
    }

    data class State(
        val historyEntries: ResourceUiState<List<HistoryEntry>>,
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

