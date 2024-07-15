package features.actresses.actresses_list

import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.actresses.GetActressesListUsecase
import domain.interactors.actresses.SearchActressesListUsecase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class ActressesListViewModel(
    private val actressesListUsecase: GetActressesListUsecase,
    private val actressSearchUsecase: SearchActressesListUsecase,
) : BaseViewModel<ActressesListContracts.Event, ActressesListContracts.State, ActressesListContracts.Effect>() {

    init {
        getDetails()
    }

    override fun createInitialState(): ActressesListContracts.State =
        ActressesListContracts.State(
            actresses = ResourceUiState.Idle,
            searchText = "",
            searchActresses = ResourceUiState.Idle,
        )

    override fun handleEvent(event: ActressesListContracts.Event) {
        when (event) {
            ActressesListContracts.Event.OnBackPressed -> setEffect { ActressesListContracts.Effect.BackNavigation }
            is ActressesListContracts.Event.OnNavigateToActressDetailRequested -> setEffect {
                ActressesListContracts.Effect.ActressDetailNavigation(
                    event.actressId
                )
            }

            is ActressesListContracts.Event.OnSearchTextChanged -> {
                searchActresses(event.searchText)
            }
        }
    }

    private fun searchActresses(searchText: String) {
        setState { copy(searchActresses = ResourceUiState.Loading, searchText = searchText) }
        screenModelScope.launch(Dispatchers.IO) {
            actressSearchUsecase(searchText)
                .collect { result ->
                    result.onSuccess { succ ->
                        setState {
                            copy(
                                searchActresses = ResourceUiState.Success(
                                    flow { emit(succ) }.cachedIn(
                                        screenModelScope
                                    )
                                )
                            )
                        }
                    }
                        .onFailure {
                            setState {
                                copy(
                                    searchActresses = ResourceUiState.Error(
                                        it.message ?: ""
                                    )
                                )
                            }
                        }
                }
        }
    }

    private fun getDetails() {
        setState { copy(actresses = ResourceUiState.Loading) }
        screenModelScope.launch(Dispatchers.IO) {
            actressesListUsecase(Unit)
                .collect { result ->
                    result.onSuccess { succ ->
                        setState {
                            copy(
                                actresses = ResourceUiState.Success(
                                    flow { emit(succ) }.cachedIn(
                                        screenModelScope
                                    )
                                )
                            )
                        }
                    }
                        .onFailure {
                            setState {
                                copy(
                                    actresses = ResourceUiState.Error(
                                        it.message ?: ""
                                    )
                                )
                            }
                        }
                }
        }
    }

}