package features.home

import androidx.paging.PagingData
import domain.model.VideoEntity
import features.home.data.VideoFeed
import kotlinx.coroutines.flow.Flow
import presentation.model.ResourceUiState
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

interface HomeContract {
    sealed interface Event : MVIIntent {
        data object OnTryCheckAgainClick : Event
        data object OnLoadDataRequested : Event
        data class OnVideoItemClicked(val itemId: String) : Event
        data class OnSearchTextChanged(val searchText: String) : Event
        data object OnBackPressed : Event
    }

    data class State(
        val videoFeeds: ResourceUiState<List<VideoFeed>>,
        val searchFeed: ResourceUiState<Flow<PagingData<VideoEntity>>>,
        val searchText: String,
    ) : MVIState

    sealed interface Effect : MVIAction {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data object BackNavigation : Effect
        data class NavigateToDetails(val id: String) : Effect
        data class NewVideoAdded(val title: String) : Effect
    }
}
