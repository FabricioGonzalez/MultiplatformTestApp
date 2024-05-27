package features

import features.home.HomeViewModel
import features.videos.video_details.VideoDetailsViewModel
import org.koin.dsl.module

val viewModelDependencies = module {
    factory { HomeViewModel(get()) }
    factory { params -> VideoDetailsViewModel(params.get(),get()) }
}