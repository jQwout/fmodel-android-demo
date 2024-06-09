package fraktal.io.android.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import fraktal.io.android.demo.chat.nav.ChatGraph
import fraktal.io.android.demo.workers.nav.WorkersGraph
import fraktal.io.ext.NavigationResult
import kotlin.math.absoluteValue

@Composable
fun BottomNavBar(
    navController: NavHostController,
) {
    val navItems = listOf(
        BottomNavItem("Workers", Icons.Filled.AccountCircle, WorkersGraph),
        BottomNavItem("Chat", Icons.Filled.MailOutline, ChatGraph)
    )

    var rememberActiveIndicator by remember(navItems) {
        mutableIntStateOf(0)
    }

    NavigationBar {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = index == rememberActiveIndicator,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                ),
                onClick = {
                    rememberActiveIndicator = index
                    navController.navigate(item.nav) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun NavHostController.currentRouteSelected(nav: NavigationResult): Boolean {
   return false // cannot compare graph and dest via typesafe nav
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val nav: NavigationResult
)