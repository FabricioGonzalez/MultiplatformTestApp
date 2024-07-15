package presentation.ui.common

import cafe.adriel.voyager.navigator.tab.Tab

interface AppTab : Tab {
    val route: String
    val onCompose: (AppBarState) -> Unit
}
