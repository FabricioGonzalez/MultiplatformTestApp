package features.home

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.videos.GetAllPreferredContentUsecase
import domain.interactors.videos.GetAllRecentVideosUsecase
import domain.interactors.videos.GetNewlyAddedVideosUsecase
import domain.interactors.videos.GetSearchVideosUsecase
import features.home.data.VideoFeed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class HomeViewModel(
    private val loadRecentVideosUsecase: GetAllRecentVideosUsecase,
    private val loadSearchVideosUsecase: GetSearchVideosUsecase,
    private val loadPreferredContentUsecase: GetAllPreferredContentUsecase,
    private val addedVideoUsecase: GetNewlyAddedVideosUsecase,
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    override fun createInitialState(): HomeContract.State = HomeContract.State(
        videoFeeds = ResourceUiState.Idle, searchFeed = ResourceUiState.Idle, searchText = ""
    )

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.OnTryCheckAgainClick -> loadAllVideos()
            is HomeContract.Event.OnSearchTextChanged -> {
                loadSearchVideos(event.searchText)
            }

            HomeContract.Event.OnBackPressed -> setEffect { HomeContract.Effect.BackNavigation }
            is HomeContract.Event.OnVideoItemClicked -> {
                setEffect { HomeContract.Effect.NavigateToDetails(event.itemId) }
            }

            HomeContract.Event.OnLoadDataRequested -> loadAllVideos()
        }
    }

    private val task = screenModelScope.launch {
        addedVideoUsecase(Unit).collectLatest { result ->
            result.onSuccess { suc ->
                suc?.let {
                    setEffect { HomeContract.Effect.NewVideoAdded(it.title) }
                }
            }

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
        setState { copy(videoFeeds = ResourceUiState.Loading) }
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
                setState {
                    copy(
                        videoFeeds = videoFeeds
                    )
                }
            }.onFailure {
                ResourceUiState.Error(it.message ?: "Erro")
            }
        }
    }


    override fun onDispose() {
        super.onDispose()
        task.cancel()
    }
}