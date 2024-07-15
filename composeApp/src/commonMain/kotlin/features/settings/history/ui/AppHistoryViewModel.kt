package features.settings.history.ui

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.video_history.ListHistoryUsecase
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class AppHistoryViewModel(
    private val listHistoryUsecase: ListHistoryUsecase
) : BaseViewModel<AppHistoryContract.Event, AppHistoryContract.State, AppHistoryContract.Effect>() {

    override fun createInitialState(): AppHistoryContract.State = AppHistoryContract.State(
        searchFeed = ResourceUiState.Idle, searchText = "", historyEntries = ResourceUiState.Idle
    )

    override fun handleEvent(event: AppHistoryContract.Event) {
        when (event) {
            AppHistoryContract.Event.OnBackPressed -> setEffect { AppHistoryContract.Effect.BackNavigation }

            AppHistoryContract.Event.OnLoadDataRequested -> loadHistory()
            is AppHistoryContract.Event.OnGoToVideoDetailsRequested -> {
                setEffect { AppHistoryContract.Effect.NavigateToDetails(event.videoId) }
            }
        }
    }

    private fun loadHistory() {
        setState { copy(historyEntries = ResourceUiState.Loading) }
        screenModelScope.launch {
            listHistoryUsecase.block(Unit).onSuccess {
                setState { copy(historyEntries = ResourceUiState.Success(it)) }
            }
                .onFailure {
                    setState { copy(historyEntries = ResourceUiState.Error(it.message)) }
                }
        }
    }
}