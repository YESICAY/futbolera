package com.example.futbolera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.futbolera.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { FutboleraApp() }
        }
    }
}

@Composable
fun FutboleraApp() {
    val nav = rememberNavController()
    val vm: FootballViewModel = viewModel()

    NavHost(navController = nav, startDestination = "search") {
        composable("search") {
            SearchScreen(vm) { team ->
                vm.selectTeam(team)
                nav.navigate("team")
            }
        }
        composable("team") {
            vm.selectedTeam?.let { team ->
                TeamScreen(
                    team = team,
                    onBack = { nav.popBackStack() },
                    onPlayers = { vm.loadPlayers(); nav.navigate("players") },
                    onTable = { vm.loadTable(); nav.navigate("table") }
                )
            }
        }
        composable("players") {
            PlayersScreen(vm, onBack = { nav.popBackStack() }) { player ->
                vm.loadPlayer(player)
                nav.navigate("player")
            }
        }
        composable("player") { PlayerScreen(vm) { nav.popBackStack() } }
        composable("table") { TableScreen(vm) { nav.popBackStack() } }
    }
}
