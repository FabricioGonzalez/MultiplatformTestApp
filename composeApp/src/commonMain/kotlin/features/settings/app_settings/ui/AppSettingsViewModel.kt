package features.settings.app_settings.ui

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.videos.GetAllPreferredContentUsecase
import domain.interactors.videos.GetAllRecentVideosUsecase
import domain.interactors.videos.GetSearchVideosUsecase
import features.home.data.VideoFeed
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class AppSettingsViewModel(
    private val loadRecentVideosUsecase: GetAllRecentVideosUsecase,
    private val loadSearchVideosUsecase: GetSearchVideosUsecase,
    private val loadPreferredContentUsecase: GetAllPreferredContentUsecase
) : BaseViewModel<AppSettingsContract.Event, AppSettingsContract.State, AppSettingsContract.Effect>() {

    override fun createInitialState(): AppSettingsContract.State = AppSettingsContract.State(
        searchFeed = ResourceUiState.Idle, searchText = ""
    )

    override fun handleEvent(event: AppSettingsContract.Event) {
        when (event) {
            AppSettingsContract.Event.OnTryCheckAgainClick -> loadAllVideos()
            is AppSettingsContract.Event.OnSearchTextChanged -> {
                loadSearchVideos(event.searchText)
            }

            AppSettingsContract.Event.OnBackPressed -> setEffect { AppSettingsContract.Effect.BackNavigation }
            is AppSettingsContract.Event.OnVideoItemClicked -> {
                setEffect { AppSettingsContract.Effect.NavigateToDetails(event.itemId) }
            }

            AppSettingsContract.Event.OnLoadDataRequested -> loadAllVideos()
        }
    }

    private fun loadSearchVideos(searchText: String) {
        setState { copy(searchFeed = ResourceUiState.Loading) }
        screenModelScope.launch {
            loadSearchVideosUsecase(searchText).onSuccess { success ->
                success.getOrNull()?.let {
                    setState {
                        copy(
                            searchFeed = ResourceUiState.Success(it), searchText = searchText
                        )
                    }
                }
            }.onFailure {
                ResourceUiState.Error(it.message)
            }
        }
    }

    private fun loadAllVideos() {
        //setState { copy(videoFeeds = ResourceUiState.Loading) }
        screenModelScope.launch {
            loadPreferredContentUsecase(Unit).onSuccess { success ->
                val videoFeeds = success.takeIf { it.isNotEmpty() }?.map { result ->
                    VideoFeed(
                        title = result.title,
                        videos = loadRecentVideosUsecase(result).fold(onSuccess = { successData ->
                            ResourceUiState.Success(successData.getOrThrow())
                        }, onFailure = { errorData ->
                            ResourceUiState.Error(errorData.message ?: "")
                        })
                    )
                }.let {
                    if (it != null) ResourceUiState.Success(it)
                    else ResourceUiState.Error("Erro")
                }
                /*setState {
                    copy(
                        videoFeeds = videoFeeds
                    )
                }*/
            }.onFailure {
                ResourceUiState.Error(it.message ?: "Erro")
            }
        }
    }
}