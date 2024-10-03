package presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AppScreenDestinations {

    @Serializable
    data object Login : AppScreenDestinations

    @Serializable
    data object Home : AppScreenDestinations

    @Serializable
    data class VideoDetails(val videoId: String) : AppScreenDestinations

    @Serializable
    data object WebLocals : AppScreenDestinations

    @Serializable
    data class WeblocalDetails(val webLocalId: String) : AppScreenDestinations

    @Serializable
    data object ActressesList : AppScreenDestinations

    @Serializable
    data class ActressPictureSearch(val actressName: String) : AppScreenDestinations

    @Serializable
    data class ActressEdit(val actressId: String) : AppScreenDestinations

    @Serializable
    data class ActressDetails(val actressId: String) : AppScreenDestinations

    @Serializable
    data object Settings : AppScreenDestinations

}