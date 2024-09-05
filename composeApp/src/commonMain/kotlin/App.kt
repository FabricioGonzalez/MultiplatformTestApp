import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import features.actresses.actress_details.ActressDetailsScreen
import features.actresses.actress_picture_search.ActressPictureSearchScreen
import features.actresses.actresses_list.ActressesListScreen
import features.home.HomeScreen
import features.login.LoginScreen
import features.settings.SettingsPage
import features.videos.video_details.VideoDetailScreen
import features.web_locals.details.WebLocalsDetailScreen
import features.web_locals.list.WebLocalsListScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.navigation.BottomNavBar
import presentation.ui.common.navigation.NavRailBar
import themes.MediaAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(isDarkTheme: Boolean = false, appColor: ColorScheme?) {
    MediaAppTheme(darkTheme = isDarkTheme, colorScheme = appColor) {
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
                icon = Icons.Rounded.PeopleAlt,
                uiScreen = ActressesListScreen(onCompose = setAppBarState),
                title = "Actress List"
            ),

            NavPoint(
                icon = Icons.Rounded.OpenInBrowser,
                isDefault = false,
                isNavigatable = true,
                disableNavBar = false,
                uiScreen = WebLocalsListScreen(onCompose = setAppBarState),
                title = "WebLocals"
            ),
            NavPoint(
                icon = Icons.Rounded.Settings,
                isDefault = false,
                isNavigatable = true,
                disableNavBar = false,
                uiScreen = SettingsPage("", onCompose = setAppBarState),
                title = "Settings"
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
                isNavigatable = false,
                disableNavBar = true,
                uiScreen = ActressDetailsScreen("", onCompose = setAppBarState),
                title = "Actress Details"
            ),
            NavPoint(
                isDefault = false,
                isNavigatable = false,
                disableNavBar = true,
                uiScreen = ActressPictureSearchScreen("", onCompose = setAppBarState),
                title = "Webview Screen"
            ),
            NavPoint(
                isDefault = false,
                isNavigatable = false,
                disableNavBar = true,
                uiScreen = WebLocalsDetailScreen("", onCompose = setAppBarState),
                title = "WebLocalDetails"
            ),
        )


        Navigator(screens.first { it.isDefault }.uiScreen) { nav ->
            val sizes = currentWindowAdaptiveInfo()
            val showNavBar =
                derivedStateOf { sizes.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT }
            Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                when {
                    appBarState.searchBar != null -> {
                        appBarState.searchBar.invoke()
                    }

                    appBarState.title != null || appBarState.actions != null || appBarState.navigationIcon != null -> {
                        TopAppBar(title = appBarState.title ?: {},
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
                    SlideTransition(navigator = nav)
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
