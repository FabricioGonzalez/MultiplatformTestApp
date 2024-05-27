import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import features.home.HomeScreen
import features.videos.video_details.VideoDetailScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.ui.common.AppScreen
import presentation.ui.common.navigation.NavRailBar
import themes.MediaAppTheme

@Composable
@Preview
fun App(isDarkTheme: Boolean = false) {
    MediaAppTheme(darkTheme = isDarkTheme) {

        val screens = listOf(
            NavPoint(
                isDefault = true,
                uiScreen = HomeScreen(),
                title = "Home"
            ),
            NavPoint(
                isDefault = false,
                isNavigatable = false,
                disableNavBar = true,
                uiScreen = VideoDetailScreen(""),
                title = "Video Details"
            ),
        )

        val (showNavBar, setShowNavBar) = remember { mutableStateOf(true) }

        Navigator(screens.first { it.isDefault }.uiScreen) { nav ->
            Scaffold(topBar = {
                nav.lastItemOrNull?.let { screen ->
                    if (screen is AppScreen) {
                        screen.TopBarContent()
                    }
                }
            }, bottomBar = {
                nav.lastItemOrNull?.let { screen ->
                    if (screen is AppScreen && !showNavBar) {
                        screen.BottomBarContent()
                    }
                }
            }) { paddings ->

                Row(modifier = Modifier.padding(paddings)) {
                    if (showNavBar && screens.firstOrNull { navPoint -> navPoint.uiScreen.key == nav.lastItemOrNull?.key }?.disableNavBar != true) {
                        NavRailBar(navigationItems = screens, navigator = nav)
                    }
                    CurrentScreen()
                }
            }
        }
    }
}


data class NavPoint(
    val isDefault: Boolean = false,
    val isNavigatable: Boolean = true,
    val uiScreen: AppScreen,
    val title: String,
    val icon: ImageVector? = null,
    val disableNavBar: Boolean = false
)
