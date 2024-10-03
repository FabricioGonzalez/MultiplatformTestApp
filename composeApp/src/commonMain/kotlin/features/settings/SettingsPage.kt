package features.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import features.settings.app_settings.ui.AppSettingsScreen
import features.settings.history.ui.AppHistoryScreen
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen
import presentation.ui.common.AppTab

class SettingsPage(
    override val route: String = "settings",
    private val navController: NavHostController,
    override val onCompose: (AppBarState) -> Unit
) : AppScreen {
    override val key: ScreenKey = "Settings"

    private val screens: List<AppTab> = listOf(
        AppSettingsScreen(onCompose = onCompose),
        AppHistoryScreen(navController = navController, onCompose = onCompose),
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        TabNavigator(screens.first { it.options.index == 0.toUShort() }) { nav ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    PrimaryTabRow(selectedTabIndex = screens.indexOf(nav.current).let {
                        if (it != -1) it
                        else 0
                    }) {
                        screens.forEach {
                            TabNavigationItem(it)
                        }
                    }
                },
                content = { paddings ->
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddings)
                    ) { CurrentTab() }
                },
            )
        }
    }
}

@Composable
private fun TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    Tab(selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        text = { Text(tab.options.title) },
        icon = {
            tab.options.icon?.let {
                Icon(
                    painter = it, contentDescription = tab.options.title
                )
            }
        })
}