package domain.interactors.videos

import domain.interactors.type.BaseUseCase
import domain.model.PreferredContentEntity
import domain.model.enums.ContentPreferrence
import domain.repositories.VideoRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetAllPreferredContentUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<Unit, List<PreferredContentEntity>>(dispatcher) {
    override suspend fun block(param: Unit): List<PreferredContentEntity> {
        return listOf(
            PreferredContentEntity("Recents", ContentPreferrence.RecentContent),
            /*PreferredContentEntity("Most Liked",ContentPreferrence.MostLiked)*/
        ) + repository.getContentPreference()
    }
}