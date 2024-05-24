package domain

import domain.interactors.videos.GetAllRecentVideosUsecase
import domain.interactors.videos.VideoSearchUsecase
import org.koin.dsl.module

val usecaseDependecies = module {
    factory {
        VideoSearchUsecase(get(), get())
        GetAllRecentVideosUsecase(get(), get())
    }
}