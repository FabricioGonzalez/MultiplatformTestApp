package features.web_locals.list

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.web_locals.DeleteWebLocalUsecase
import domain.interactors.web_locals.GetWebLocalsListUsecase
import features.web_locals.list.components.Site
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class WebLocalsListViewModel(
    private val getWebLocalsListUsecase: GetWebLocalsListUsecase,
    private val deleteWebLocalUsecase: DeleteWebLocalUsecase
) : BaseViewModel<WebLocalsListContracts.Event, WebLocalsListContracts.State, WebLocalsListContracts.Effect>() {

    override fun createInitialState(): WebLocalsListContracts.State = WebLocalsListContracts.State(
        sites = ResourceUiState.Idle,
        isFavorite = ResourceUiState.Idle,
    )

    override fun handleEvent(event: WebLocalsListContracts.Event) {
        when (event) {
            is WebLocalsListContracts.Event.OnLoadDataRequested -> loadData()
            WebLocalsListContracts.Event.OnBackPressed -> setEffect { WebLocalsListContracts.Effect.BackNavigation }
            is WebLocalsListContracts.Event.NavigateToWebLocalRequested -> setEffect {
                WebLocalsListContracts.Effect.NavigateToWebLocalRequested(
                    event.id
                )
            }

            is WebLocalsListContracts.Event.NavigateToWebLocalDetailsRequested -> setEffect {
                WebLocalsListContracts.Effect.NavigateToWebLocalDetailsRequested(
                    event.id
                )
            }

            is WebLocalsListContracts.Event.OnDeleteRequested -> delete(event.id)
        }
    }

    private fun delete(id: String) {
        screenModelScope.launch {
            if (id.isNotEmpty()) {
                deleteWebLocalUsecase(id).onSuccess {
                    setState {
                        if (sites is ResourceUiState.Success) {
                            copy(
                                sites = sites.copy(data = sites.data.filter { it.id != id })
                            )
                        } else copy()
                    }

                }
            }
        }
    }

    private fun loadData() {
        setState { copy(sites = ResourceUiState.Loading) }
        screenModelScope.launch {
            getWebLocalsListUsecase(Unit).onSuccess { succ ->
                setState {
                    copy(sites = ResourceUiState.Success(succ.map {
                        Site(
                            id = it.id, url = it.url, name = it.name
                        )
                    }))
                }
            }.onFailure {
                setState { copy(sites = ResourceUiState.Error(it.message)) }
            }
        }

    }

}