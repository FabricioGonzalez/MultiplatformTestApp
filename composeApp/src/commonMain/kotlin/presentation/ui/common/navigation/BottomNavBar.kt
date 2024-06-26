package presentation.ui.common.navigation

import NavPoint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import presentation.ui.common.AppScreen

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    navigator: Navigator,
    navigationItems: List<NavPoint>
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .85f)
    ) {
        navigationItems.filter { it.isNavigatable }.forEach { item ->
            
            val currentDestination = navigator.lastItemOrNull as AppScreen
            val isSelected = item.uiScreen.route == currentDestination.route

            NavigationBarItem(
                icon = {
                    item.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = item.title
                        )
                    }
                },
                label = { Text(text = item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface
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
