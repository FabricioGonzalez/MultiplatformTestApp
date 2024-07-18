package features.web_locals.details

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.web_locals.GetWebLocalUsecase
import domain.interactors.web_locals.ModifyWebLocalUsecase
import domain.model.SiteEntity
import features.web_locals.list.components.Site
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class WebLocalsDetailsViewModel(
    private val modifyWebLocalUsecase: ModifyWebLocalUsecase,
    private val getWebLocalUsecase: GetWebLocalUsecase,
) : BaseViewModel<WebLocalsDetailsContracts.Event, WebLocalsDetailsContracts.State, WebLocalsDetailsContracts.Effect>() {

    override fun createInitialState(): WebLocalsDetailsContracts.State = WebLocalsDetailsContracts.State(
        site = ResourceUiState.Idle,
        isFavorite = ResourceUiState.Idle,
    )

    override fun handleEvent(event: WebLocalsDetailsContracts.Event) {
        when (event) {
            is WebLocalsDetailsContracts.Event.OnLoadDataRequested -> getDetails(event.webLocalId)
            WebLocalsDetailsContracts.Event.OnBackPressed -> setEffect { WebLocalsDetailsContracts.Effect.BackNavigation }
            is WebLocalsDetailsContracts.Event.OnNameChanged -> {

                setState {
                    if (site is ResourceUiState.Success) {
                        copy(
                            site = site.copy(
                                data = site.data.copy(
                                    name = event.name
                                )
                            )
                        )
                    } else copy()
                }
            }

            is WebLocalsDetailsContracts.Event.OnUrlChanged -> {
                setState {
                    if (site is ResourceUiState.Success) {
                        copy(
                            site = site.copy(
                                data = site.data.copy(
                                    url = event.url
                                )
                            )
                        )
                    } else copy()
                }
            }

            WebLocalsDetailsContracts.Event.OnSaveRequested -> {
                if (this.uiState.value.site is ResourceUiState.Success) {
                    modifyWebLocal((this.uiState.value.site as ResourceUiState.Success<Site>).data)
                }
            }
        }
    }

    private fun modifyWebLocal(site: Site) {
        screenModelScope.launch {
            modifyWebLocalUsecase(site.let {
                SiteEntity(
                    it.id,
                    it.name,
                    it.url
                )
            }).onSuccess { succ ->
                setState {
                    if (this.site is ResourceUiState.Success) {
                        copy(
                            site = this.site.copy(
                                data = this.site.data.copy(
                                    id = succ?.id,
                                    name = succ?.name ?: "",
                                    url = succ?.url ?: ""
                                )
                            )
                        )
                    } else copy()
                }
            }
        }
    }

    private fun getDetails(id: String?) {
        setState { copy(site = ResourceUiState.Loading) }
        screenModelScope.launch {
            if (id.isNullOrBlank()) {
                setState {
                    copy(
                        site = ResourceUiState.Success(
                            Site(
                                id = null, name = "", url = ""
                            )
                        )
                    )
                }
            } else getWebLocalUsecase(id).onSuccess { succ ->
                succ?.let {
                    setState {
                        copy(
                            site = ResourceUiState.Success(
                                Site(
                                    id = succ.id, name = succ.name, url = succ.url
                                )
                            )
                        )
                    }
                } ?: setState { copy(site = ResourceUiState.Error()) }
            }.onFailure { setState { copy(site = ResourceUiState.Error(it.message)) } }
        }
    }

}