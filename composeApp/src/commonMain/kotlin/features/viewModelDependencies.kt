package features

import features.actresses.actress_details.ActressDetailsViewModel
import features.actresses.actresses_list.ActressesListViewModel
import features.home.HomeViewModel
import features.videos.video_details.VideoDetailsViewModel
import org.koin.dsl.module

val viewModelDependencies = module {
    factory { HomeViewModel(get()) }
    factory { params -> VideoDetailsViewModel(params.get(),get()) }
    factory { ActressesListViewModel(get()) }
    factory { params -> ActressDetailsViewModel(params.get(),get()) }

}