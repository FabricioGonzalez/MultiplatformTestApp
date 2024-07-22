package domain

import domain.interactors.actresses.*
import domain.interactors.tags.FavoriteTagUsecase
import domain.interactors.video_history.ListHistoryUsecase
import domain.interactors.video_history.WriteToVideoHistoryUsecase
import domain.interactors.videos.*
import domain.interactors.web_locals.DeleteWebLocalUsecase
import domain.interactors.web_locals.GetWebLocalUsecase
import domain.interactors.web_locals.GetWebLocalsListUsecase
import domain.interactors.web_locals.ModifyWebLocalUsecase
import org.koin.dsl.module

val usecaseDependecies = module {
    /*factory {
        VideoSearchUsecase(get(), get())
    }*/
    factory {
        GetVideosByTagUsecase(get(), get())
    }
    factory { GetVideoDetailsUsecase(get(), get()) }
    factory { GetNewlyAddedVideosUsecase(get(), get()) }
    factory { GetAllPreferredContentUsecase(get(), get()) }
    factory { SearchActressesListUsecase(get(), get()) }
    factory { GetSearchVideosUsecase(get(), get()) }
    factory { FavoriteActressUsecase(get(), get()) }
    factory { GetActressesListUsecase(get(), get()) }
    factory { GetAllRecentVideosUsecase(get(), get()) }
    factory { GetActressDetailsUsecase(get(), get()) }
    factory { GetVideosByTagUsecase(get(), get()) }
    factory { GetVideosByActressUsecase(get(), get()) }
    factory { UpdateActressUsecase(get(), get()) }
    factory { FavoriteTagUsecase(get(), get()) }
    factory { WriteToVideoHistoryUsecase(get(), get()) }
    factory { ListHistoryUsecase(get(), get()) }
    factory { GetWebLocalsListUsecase(get(), get()) }
    factory { ModifyWebLocalUsecase(get(), get()) }
    factory { GetWebLocalUsecase(get(), get()) }
    factory { DeleteWebLocalUsecase(get(), get()) }
}