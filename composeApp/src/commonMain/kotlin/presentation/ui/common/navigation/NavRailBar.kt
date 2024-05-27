package presentation.ui.common.navigation

import NavPoint
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import cafe.adriel.voyager.navigator.Navigator
import presentation.ui.common.AppScreen

@Composable
fun NavRailBar(
    modifier: Modifier = Modifier,
    navigator: Navigator,
    navigationItems: List<NavPoint>
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight().alpha(0.95F),
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            /*Icon(
                modifier = Modifier.size(42.dp),
                painter = painterResource("n_logo.png"),
                contentDescription = "Logo"
            )*/
        },
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        navigationItems.filter { it.isNavigatable}.forEach { item ->

            val currentDestination =navigator.lastItemOrNull as AppScreen 
            val isSelected = item.uiScreen.route == currentDestination.route

            NavigationRailItem(
                icon = {
                    item.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = item.title
                        )
                    }
                },
                label = { Text(text = item.title) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.background
                ),
                alwaysShowLabel = true,
                selected = isSelected,
                onClick = {
                    if (item.uiScreen != currentDestination) navigator.push(item.uiScreen)
                }
            )
        }
    }
}
