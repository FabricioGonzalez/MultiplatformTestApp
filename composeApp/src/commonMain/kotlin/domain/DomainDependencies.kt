package domain

import domain.interactors.videos.GetAllRecentVideosUsecase
import domain.interactors.videos.GetVideoDetailsUsecase
import domain.interactors.videos.VideoSearchUsecase
import org.koin.dsl.module

val usecaseDependecies = module {
    factory {
        VideoSearchUsecase(get(), get())
    }
    factory {
        GetAllRecentVideosUsecase(get(), get())
    }
    factory { GetVideoDetailsUsecase(get(), get()) }
}