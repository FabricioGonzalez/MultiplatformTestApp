package features.actresses.actresses_list

import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.screenModelScope
import daniel.avila.rnm.kmm.presentation.mvi.BaseViewModel
import domain.interactors.actresses.GetActressesListUsecase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState

class ActressesListViewModel(
    private val actressesListUsecase: GetActressesListUsecase
) : BaseViewModel<ActressesListContracts.Event, ActressesListContracts.State, ActressesListContracts.Effect>() {

    init {
        getDetails()
    }
    override fun createInitialState(): ActressesListContracts.State =
        ActressesListContracts.State(
            actresses = ResourceUiState.Idle,
        )

    override fun handleEvent(event: ActressesListContracts.Event) {
        when (event) {
            ActressesListContracts.Event.OnBackPressed -> setEffect { ActressesListContracts.Effect.BackNavigation }
        }
    }

    private fun getDetails() {
        setState { copy(actresses = ResourceUiState.Loading) }
        screenModelScope.launch {
            actressesListUsecase(Unit)
                .collect { result ->
                    result.onSuccess { succ ->
                        setState { copy(actresses = ResourceUiState.Success(flow { emit(succ) }.cachedIn(screenModelScope))) }
                    }
                        .onFailure { setState { copy(actresses = ResourceUiState.Error()) } }
                }
        }
    }
   
}