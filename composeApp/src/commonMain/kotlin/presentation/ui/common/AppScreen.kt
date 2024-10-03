package presentation.ui.common

import NavPoint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

interface AppScreen : Screen {
    val route: String
    val onCompose: (AppBarState) -> Unit

    @Composable
    fun NavigationContent() {
    }

    @Composable
    fun BottomBarContent(navigationItems: List<NavPoint>) {
       
    }
}

data class AppBarState(
    val title: (@Composable () -> Unit)? = null,
    val navigationIcon: (@Composable () -> Unit)? = null,
    val actions: (@Composable RowScope.() -> Unit)? = null,
    val searchBar: (@Composable () -> Unit)? = null,
    val snackbarHost: (@Composable () -> Unit)? = null
)