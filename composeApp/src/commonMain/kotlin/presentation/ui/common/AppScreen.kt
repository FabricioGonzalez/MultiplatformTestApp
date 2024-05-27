package presentation.ui.common

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

interface AppScreen : Screen {
    val route: String

    @Composable
    fun TopBarContent() {

    }

    @Composable
    fun NavigationContent() {
    }

    @Composable
    fun BottomBarContent() {

    }
}