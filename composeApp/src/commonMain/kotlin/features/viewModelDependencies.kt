package features

import features.actresses.actress_details.ActressDetailsViewModel
import features.actresses.actresses_list.ActressesListViewModel
import features.home.HomeViewModel
import features.videos.video_details.VideoDetailsViewModel
import features.webview.WebviewViewModel
import org.koin.dsl.module

val viewModelDependencies = module {
    factory { HomeViewModel(get(), get()) }
    factory { VideoDetailsViewModel(get(), get(), get()) }
    factory { ActressesListViewModel(get()) }
    factory { params -> ActressDetailsViewModel(params.get(), get(), get(), get(), get()) }
    factory { WebviewViewModel(get()) }

}