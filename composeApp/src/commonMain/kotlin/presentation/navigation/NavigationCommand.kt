package presentation.navigation

import androidx.navigation.NavOptions

sealed class NavigationCommand {
    object NavigateUp : NavigationCommand()
}

data class NavigationOptions(val authorizationSpecs: List<String>, val options: NavOptions? = null)
sealed class ComposeNavigationCommand : NavigationCommand() {
    data class NavigateToRoute(val route: String, val options: NavigationOptions? = null) :
        ComposeNavigationCommand()

    data class NavigateUpWithResult<T>(
        val key: String,
        val result: T,
        val route: String? = null,
    ) : ComposeNavigationCommand()

    data class PopUpToRoute(val route: String, val inclusive: Boolean) : ComposeNavigationCommand()
    data object PopBackStack : ComposeNavigationCommand()
}