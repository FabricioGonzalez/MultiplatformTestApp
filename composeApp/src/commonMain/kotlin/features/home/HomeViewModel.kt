package features.home

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.videos.GetAllPreferredContentUsecase
import domain.interactors.videos.GetAllRecentVideosUsecase
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class HomeViewModel(
    private val loadRecentVideosUsecase: GetAllRecentVideosUsecase,
    private val loadPreferredContentUsecase: GetAllPreferredContentUsecase
) :
    BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    override fun createInitialState(): HomeContract.State =
        HomeContract.State(
            videoFeeds = ResourceUiState.Idle,
        )

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.OnTryCheckAgainClick -> loadAllVideos()
            HomeContract.Event.OnBackPressed -> setEffect { HomeContract.Effect.BackNavigation }
            is HomeContract.Event.OnVideoItemClicked -> {
                setEffect { HomeContract.Effect.NavigateToDetails(event.itemId) }
            }

            HomeContract.Event.OnLoadDataRequested -> loadAllVideos()
        }
    }

    private fun loadAllVideos() {
        setState { copy(videoFeeds = ResourceUiState.Loading) }
        screenModelScope.launch {
            loadPreferredContentUsecase(Unit)
                .onSuccess { success ->
                    val videoFeeds = success.takeIf { it.isNotEmpty() }?.map { result ->
                        VideoFeed(title = result.title,
                            videos = loadRecentVideosUsecase(result).fold(
                                onSuccess = { successData ->
                                    ResourceUiState.Success(successData.getOrThrow())
                                },
                                onFailure = { errorData ->
                                    ResourceUiState.Error(errorData.message ?: "")
                                }
                            ))
                    }.let {
                        if (it != null) ResourceUiState.Success(it)
                        else ResourceUiState.Error("Erro")
                    }
                    setState {
                        copy(
                            videoFeeds = videoFeeds
                        )
                    }
                }
                .onFailure {
                    ResourceUiState.Error(it.message ?: "Erro")
                }
        }
    }
}