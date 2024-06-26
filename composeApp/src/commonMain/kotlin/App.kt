import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import features.actresses.actress_details.ActressDetailsScreen
import features.actresses.actresses_list.ActressesListScreen
import features.home.HomeScreen
import features.login.LoginScreen
import features.settings.ui.SettingsScreen
import features.videos.video_details.VideoDetailScreen
import features.webview.WebviewScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.navigation.BottomNavBar
import presentation.ui.common.navigation.NavRailBar
import themes.MediaAppTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(isDarkTheme: Boolean = false, appColor: Color?) {
    MediaAppTheme(darkTheme = isDarkTheme, appColor = appColor) {
        val (appBarState, setAppBarState) = remember {
            mutableStateOf(AppBarState())
        }


        val screens = listOf(
            NavPoint(
                isDefault = true,
                isNavigatable = false,
                disableNavBar = true,
                icon = Icons.AutoMirrored.Rounded.Login,
                uiScreen = LoginScreen(onCompose = setAppBarState),
                title = "Login"
            ),
            NavPoint(
                isDefault = false,
                icon = Icons.Rounded.Home,
                uiScreen = HomeScreen(onCompose = setAppBarState),
                title = "Home"
            ),
            NavPoint(
                isDefault = false,
                isNavigatable = false,
                disableNavBar = true,
                uiScreen = VideoDetailScreen("", onCompose = setAppBarState),
                title = "Video Details"
            ),
            NavPoint(
                isDefault = false,
                icon = Icons.Rounded.PeopleAlt,
                uiScreen = ActressesListScreen(onCompose = setAppBarState),
                title = "Actress List"
            ),
            NavPoint(
                isDefault = false,
                isNavigatable = false,
                disableNavBar = true,
                uiScreen = ActressDetailsScreen("", onCompose = setAppBarState),
                title = "Actress Details"
            ),
            NavPoint(
                isDefault = false,
                isNavigatable = false,
                disableNavBar = true,
                uiScreen = WebviewScreen("", onCompose = setAppBarState),
                title = "WebviewScreen"
            ),
            NavPoint(
                icon = Icons.Rounded.Settings,
                isDefault = false,
                isNavigatable = true,
                disableNavBar = false,
                uiScreen = SettingsScreen("", onCompose = setAppBarState),
                title = "Settings"
            ),
        )


        Navigator(screens.first { it.isDefault }.uiScreen) { nav ->
            val sizes = calculateWindowSizeClass()
            val showNavBar =
                derivedStateOf { sizes.widthSizeClass != WindowWidthSizeClass.Compact }
            Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                when {
                    appBarState.searchBar != null -> {
                        appBarState.searchBar.invoke()
                    }

                    appBarState.title != null || appBarState.actions != null || appBarState.navigationIcon != null -> {
                        TopAppBar(
                            title = appBarState.title ?: {},
                            navigationIcon = appBarState.navigationIcon ?: {},
                            actions = appBarState.actions ?: {})
                    }

                    else -> {}
                }
            }, snackbarHost = { appBarState.snackbarHost?.invoke() }, bottomBar = {
                nav.lastItemOrNull?.let { screen ->
                    if (!showNavBar.value && screens.firstOrNull { navPoint -> navPoint.uiScreen.key == nav.lastItemOrNull?.key }?.disableNavBar != true) {
                        if (screen is AppScreen) {
                            screen.BottomBarContent(navigationItems = screens)
                        } else BottomAppBar {
                            BottomNavBar(navigator = nav, navigationItems = screens)
                        }
                    }
                }
            }) { paddings ->

                Row(modifier = Modifier.padding(paddings)) {
                    if (showNavBar.value && screens.firstOrNull { navPoint -> navPoint.uiScreen.key == nav.lastItemOrNull?.key }?.disableNavBar != true) {
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
