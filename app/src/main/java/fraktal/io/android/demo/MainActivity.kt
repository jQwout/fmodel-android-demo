package fraktal.io.android.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import fraktal.io.android.demo.chat.nav.chatGraph
import fraktal.io.android.demo.nav.NavLocator
import fraktal.io.android.demo.nav.handle
import fraktal.io.android.demo.workers.nav.WorkersGraph
import fraktal.io.android.demo.workers.nav.workerGraph
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val navManager = NavLocator.navManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val uiScope = rememberCoroutineScope()
            Scaffold(
                bottomBar = { BottomNavBar(navController = navController) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .navigationBarsPadding()
                ) {
                    NavHost(navController, startDestination = WorkersGraph) {
                        workerGraph(navController)
                        chatGraph(navController)
                    }
                }
            }
            LaunchedEffect(Unit) {
                uiScope.launch {
                    navManager.navResult.collect {
                        navController.handle(it)
                    }
                }
            }
        }
    }
}

