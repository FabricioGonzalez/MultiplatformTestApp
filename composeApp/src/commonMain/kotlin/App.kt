import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import presentation.navigation.AppScreenDestinations
import presentation.ui.common.AppBarState
import themes.MediaAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(isDarkTheme: Boolean = false, appColor: ColorScheme?) {
    MediaAppTheme(darkTheme = isDarkTheme, colorScheme = appColor) {
        val (appBarState, setAppBarState) = remember {
            mutableStateOf(AppBarState())
        }
        val navHostController = rememberNavController()

        val screens = listOf(
            NavPoint(
                isDefault = false,
                icon = Icons.Rounded.Home,
                title = "Home",
                navigationPoint = AppScreenDestinations.Home,
                onClick = { navHostController.navigate(AppScreenDestinations.Home) }
            ),

            NavPoint(
                isDefault = false,
                icon = Icons.Rounded.PeopleAlt,
                title = "Actress List",
                navigationPoint = AppScreenDestinations.ActressesList,
                onClick = { navHostController.navigate(AppScreenDestinations.ActressesList) }
            ),

            NavPoint(
                icon = Icons.Rounded.OpenInBrowser,
                isDefault = false,
                isNavigatable = true,
                disableNavBar = false,
                title = "WebLocals",
                navigationPoint = AppScreenDestinations.WebLocals,
                onClick = { navHostController.navigate(AppScreenDestinations.WebLocals) }
            ),
            NavPoint(
                icon = Icons.Rounded.Settings,
                isDefault = false,
                isNavigatable = true,
                disableNavBar = false,
                title = "Settings",
                navigationPoint = AppScreenDestinations.Settings,
                onClick = { navHostController.navigate(AppScreenDestinations.Settings) }
            ),
        )

        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationSuiteScaffold(navigationSuiteItems = {
            screens.forEach { navigation ->
                val isSelected by derivedStateOf { currentRoute == navigation.navigationPoint::class.qualifiedName }
                item(isSelected, icon = {
                    navigation.icon?.let {
                        Icon(
                            imageVector = navigation.icon,
                            contentDescription = navigation.title
                        )
                    }
                }, onClick = {
                    navigation.onClick()
                }, label = { Text(text = navigation.title, textAlign = TextAlign.Center) })
            }
        }) {
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
            },
                snackbarHost = { appBarState.snackbarHost?.invoke() },
                bottomBar = {
                    /*  nav.lastItemOrNull?.let { screen ->
                          if (!showNavBar.value && screens.firstOrNull { navPoint -> navPoint.uiScreen.key == nav.lastItemOrNull?.key }?.disableNavBar != true) {
                              if (screen is AppScreen) {
                                  screen.BottomBarContent(navigationItems = screens)
                              } else BottomAppBar {
                                  BottomNavBar(navigator = nav, navigationItems = screens)
                              }
                          }
                      }*/
                }) { paddings ->
                NavHost(
                    modifier = Modifier.padding(paddings),
                    navController = navHostController,
                    startDestination = AppScreenDestinations.Home
                ) {
                    composable<AppScreenDestinations.Home> {
                        HomeScreen(
                            navController = navHostController,
                            onCompose = setAppBarState
                        ).Content()
                    }
                    composable<AppScreenDestinations.Login> {
                        LoginScreen(
                            navController = navHostController,
                            onCompose = setAppBarState
                        ).Content()
                    }
                    composable<AppScreenDestinations.VideoDetails> { nav ->
                        val args = nav.toRoute<AppScreenDestinations.VideoDetails>()
                        VideoDetailScreen(
                            videoId = args.videoId,
                            navController = navHostController,
                            onCompose = setAppBarState
                        )
                    }
                    composable<AppScreenDestinations.WebLocals> {
                        WebLocalsListScreen(
                            navController = navHostController,
                            onCompose = setAppBarState
                        ).Content()
                    }
                    composable<AppScreenDestinations.WeblocalDetails> { nav ->
                        val args = nav.toRoute<AppScreenDestinations.WeblocalDetails>()
                        WebLocalsDetailScreen(
                            args.webLocalId,
                            navController = navHostController,
                            onCompose = setAppBarState
                        ).Content()
                    }
                    composable<AppScreenDestinations.ActressesList> {
                        ActressesListScreen(
                            navController = navHostController,
                            onCompose = setAppBarState
                        ).Content()
                    }
                    composable<AppScreenDestinations.ActressPictureSearch> { nav ->
                        val args = nav.toRoute<AppScreenDestinations.ActressPictureSearch>()
                        ActressPictureSearchScreen(
                            args.actressName,
                            navController = navHostController,
                            onCompose = setAppBarState
                        ).Content()
                    }
                    composable<AppScreenDestinations.ActressDetails> { nav ->
                        val args = nav.toRoute<AppScreenDestinations.ActressDetails>()
                        ActressDetailsScreen(
                            args.actressId,
                            navController = navHostController,
                            onCompose = setAppBarState
                        ).Content()
                    }
                    composable<AppScreenDestinations.Settings> {
                        SettingsPage(
                            navController = navHostController,
                            onCompose = setAppBarState
                        ).Content()
                    }
                }
            }
        }

    }
}

data class NavPoint(
    val isDefault: Boolean = false,
    val isNavigatable: Boolean = true,
    val navigationPoint: AppScreenDestinations,
    val onClick: () -> Unit,
    val title: String,
    val icon: ImageVector? = null,
    val disableNavBar: Boolean = false
)
