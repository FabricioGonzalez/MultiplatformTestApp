package domain.interactors.videos

import data.repositories.VideoRepository
import domain.interactors.type.BaseUseCaseFlow
import domain.model.Character
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class VideoSearchUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCaseFlow<String, List<Character>>(dispatcher) {
    override suspend fun build(param: String): Flow<List<Character>> = repository.searchByVideos(param)
}