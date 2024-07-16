package features.navigation

import cafe.adriel.voyager.navigator.Navigator
import features.actresses.actress_details.ActressDetailsScreen
import features.actresses.actresses_list.ActressesListScreen
import features.home.HomeScreen
import features.settings.SettingsPage
import features.videos.video_details.VideoDetailScreen
import presentation.ui.common.AppBarState

fun Navigator.navigateToHome(onCompose: (AppBarState) -> Unit) {
    this.replace(HomeScreen(onCompose = onCompose))
}

fun Navigator.navigateToSettings(onCompose: (AppBarState) -> Unit) {
    this.push(SettingsPage(onCompose = onCompose))
}

fun Navigator.navigateToVideoDetails(videoId: String, onCompose: (AppBarState) -> Unit) {
    this.push(VideoDetailScreen(videoId, onCompose = onCompose))
}

fun Navigator.navigateToActressesList(onCompose: (AppBarState) -> Unit) {
    this.push(ActressesListScreen(onCompose = onCompose))
}

fun Navigator.navigateToActressesDetails(actressId: String, onCompose: (AppBarState) -> Unit) {
    this.push(ActressDetailsScreen(actressId, onCompose = onCompose))
}