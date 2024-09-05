package features

import features.actresses.actress_details.ActressDetailsViewModel
import features.actresses.actress_picture_search.ActressPictureSearchViewModel
import features.actresses.actresses_list.ActressesListViewModel
import features.home.HomeViewModel
import features.settings.app_settings.ui.AppSettingsViewModel
import features.settings.history.ui.AppHistoryViewModel
import features.videos.video_details.VideoDetailsViewModel
import features.web_locals.details.WebLocalsDetailsViewModel
import features.web_locals.list.WebLocalsListViewModel
import org.koin.dsl.module

val viewModelDependencies = module {
    factory { HomeViewModel(get(), get(), get(), get()) }
    factory { VideoDetailsViewModel(get(), get(), get()) }
    factory { ActressesListViewModel(get(), get()) }
    factory { ActressDetailsViewModel(get(), get(), get(), get()) }
    factory { ActressPictureSearchViewModel(get()) }
    factory { AppSettingsViewModel(get(), get(), get()) }
    factory { AppHistoryViewModel(get()) }
    factory { AppHistoryViewModel(get()) }
    factory { WebLocalsListViewModel(get(), get()) }
    factory { WebLocalsDetailsViewModel(get(), get()) }
    factory { AppHistoryViewModel(get()) }

}