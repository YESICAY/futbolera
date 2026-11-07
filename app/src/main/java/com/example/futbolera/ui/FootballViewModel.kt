package com.example.futbolera.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolera.data.ApiClient
import com.example.futbolera.data.Player
import com.example.futbolera.data.TableRow
import com.example.futbolera.data.Team
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class FootballViewModel : ViewModel() {

    var teams by mutableStateOf<UiState<List<Team>>>(UiState.Idle)
        private set
    var selectedTeam by mutableStateOf<Team?>(null)
        private set
    var players by mutableStateOf<UiState<List<Player>>>(UiState.Idle)
        private set
    var table by mutableStateOf<UiState<List<TableRow>>>(UiState.Idle)
        private set
    var selectedPlayer by mutableStateOf<UiState<Player>>(UiState.Idle)
        private set

    fun searchTeams(name: String) {
        if (name.isBlank()) return
        teams = UiState.Loading
        viewModelScope.launch {
            teams = try {
                val result = ApiClient.api.searchTeams(name.trim()).teams
                if (result.isNullOrEmpty()) UiState.Error("No se encontraron equipos con ese nombre")
                else UiState.Success(result)
            } catch (e: Exception) {
                UiState.Error(errorMessage(e))
            }
        }
    }

    fun selectTeam(team: Team) {
        selectedTeam = team
        players = UiState.Idle
        table = UiState.Idle
    }

    fun loadPlayers() {
        val id = selectedTeam?.idTeam ?: return
        if (players is UiState.Success) return
        players = UiState.Loading
        viewModelScope.launch {
            players = try {
                val result = ApiClient.api.players(id).player
                if (result.isNullOrEmpty()) UiState.Error("Este equipo no tiene jugadores registrados")
                else UiState.Success(result)
            } catch (e: Exception) {
                UiState.Error(errorMessage(e))
            }
        }
    }

    fun loadTable() {
        val leagueId = selectedTeam?.idLeague
        if (leagueId.isNullOrBlank()) {
            table = UiState.Error("El equipo no tiene liga asociada")
            return
        }
        if (table is UiState.Success) return
        table = UiState.Loading
        viewModelScope.launch {
            table = try {
                val result = ApiClient.api.table(leagueId).table
                if (result.isNullOrEmpty()) UiState.Error("No hay tabla de posiciones disponible para esta liga")
                else UiState.Success(result)
            } catch (e: Exception) {
                UiState.Error(errorMessage(e))
            }
        }
    }

    fun loadPlayer(fromList: Player) {
        val id = fromList.idPlayer ?: return
        selectedPlayer = UiState.Success(fromList)
        viewModelScope.launch {
            try {
                val detail = ApiClient.api.player(id).players?.firstOrNull()
                if (detail != null) selectedPlayer = UiState.Success(detail)
            } catch (e: Exception) {
            }
        }
    }

    private fun errorMessage(e: Exception): String {
        val msg = e.message ?: ""
        return when {
            e is retrofit2.HttpException ->
                if (e.code() == 429) "Demasiadas consultas, espera un minuto e intenta de nuevo"
                else "Error del servidor (${e.code()})"
            e is java.io.EOFException || e is com.google.gson.JsonParseException ||
                    msg.contains("End of input", ignoreCase = true) ->
                "La API no devolvió datos para esta consulta (límite de la clave gratuita). " +
                        "Prueba con otro equipo."
            else -> "Error de red: $msg"
        }
    }
}