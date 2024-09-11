package features.actresses.actresses_list

import androidx.paging.PagingData
import domain.interactors.actresses.GetActressesListUsecase
import domain.interactors.actresses.SearchActressesListUsecase
import domain.model.ActressEntity
import features.actresses.actresses_list.ActressListContainer.ActressListAction
import features.actresses.actresses_list.ActressListContainer.ActressListIntent
import features.actresses.actresses_list.ActressListContainer.ActressListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import presentation.model.ResourceUiState
import pro.respawn.flowmvi.api.ActionShareBehavior
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.loggingPlugin
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import kotlin.jvm.JvmInline

class ActressListContainer(
    private val actressesListUsecase: GetActressesListUsecase,
    private val actressSearchUsecase: SearchActressesListUsecase,
) : Container<ActressListState, ActressListIntent, ActressListAction> {
    override val store =
        store(
            ActressListState.Loading
        ) { // set initial state
            configure {
                actionShareBehavior = ActionShareBehavior.Distribute()
                debuggable = true

                // makes the store fully async, parallel and thread-safe
                parallelIntents = true
                coroutineContext = Dispatchers.Default
                atomicStateUpdates = true
            }

            enableLogging()
            install(loggingPlugin("ActressList"))
            
            // performs long-running tasks on startup
            init {
                intent(ActressListIntent.LoadData)
            }

            // handles any errors
            recover { e: Exception ->
                action(ActressListAction.ShowMessage(e.message ?: "Erro"))
                null
            }

            reduce { intent ->
                when (intent) {
                    ActressListIntent.LoadData -> {
                        withContext(Dispatchers.IO) { actressesListUsecase(Unit) }.collect { result ->
                            result.onSuccess { succ ->
                                updateState {
                                    ActressListState.DisplayList(
                                        ResourceUiState.Success(
                                            flow { emit(succ) }
                                        )
                                    )
                                }
                            }
                                .onFailure {
                                    updateState {
                                        ActressListState.Error(
                                            it.message ?: ""
                                        )
                                    }
                                }
                        }
                    }

                    ActressListIntent.OnBackPressed -> action(ActressListAction.NavigateBack)
                    is ActressListIntent.OnNavigateToActressDetailRequested ->
                        action(ActressListAction.NavigateToDetails(intent.actressId))

                    is ActressListIntent.OnSearchTextChanged -> {
                        updateStateImmediate {
                            ActressListState.DisplaySearchList(
                                searchText = intent.searchText,
                                searchActresses = ResourceUiState.Loading
                            )
                        }
                        updateState {
                            (this as ActressListState.DisplaySearchList).copy(
                                searchActresses = actressSearchUsecase.invoke(intent.searchText)
                                    .transform { result ->
                                        emit(result.fold(
                                            { succ -> ResourceUiState.Success(flowOf(succ)) },
                                            { fail -> ResourceUiState.Error(fail.message) }
                                        ))
                                    }.first()
                            )
                        }
                    }
                }
            }
        }

    // Must be comparable and immutable. Automatically marked as stable in Compose
    sealed interface ActressListState : MVIState {
        data object Loading : ActressListState
        data class Error(val e: String) : ActressListState
        data class DisplayList(val actresses: ResourceUiState<Flow<PagingData<ActressEntity>>>) :
            ActressListState

        data class DisplaySearchList(
            val searchActresses: ResourceUiState<Flow<PagingData<ActressEntity>>>,
            val searchText: String
        ) : ActressListState
    }

    // MVI Style Intents
    sealed interface ActressListIntent : MVIIntent {
        data object OnBackPressed : ActressListIntent
        data object LoadData : ActressListIntent

        @JvmInline
        value class OnNavigateToActressDetailRequested(val actressId: String) : ActressListIntent

        @JvmInline
        value class OnSearchTextChanged(val searchText: String) : ActressListIntent

    }

    sealed interface ActressListAction : MVIAction {

        data object NavigateBack : ActressListAction
        data class ShowMessage(val message: String) : ActressListAction
        data class NavigateToDetails(val actressId: String) : ActressListAction
    }
}