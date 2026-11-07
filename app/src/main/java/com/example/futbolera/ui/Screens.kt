package com.example.futbolera.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.futbolera.data.Player
import com.example.futbolera.data.TableRow
import com.example.futbolera.data.Team
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(title: String, onBack: (() -> Unit)? = null, content: @Composable (Modifier) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                }
            )
        }
    ) { padding -> content(Modifier.padding(padding)) }
}

@Composable
fun <T> StateContent(state: UiState<T>, content: @Composable (T) -> Unit) {
    when (state) {
        UiState.Idle -> {}
        UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is UiState.Error -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(state.message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
        }
        is UiState.Success -> content(state.data)
    }
}


@Composable
fun SearchScreen(vm: FootballViewModel, onTeamClick: (Team) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    AppScaffold("Buscar equipo") { mod ->
        Column(mod.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Nombre del equipo") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.searchTeams(query) }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }
            Spacer(Modifier.height(12.dp))
            StateContent(vm.teams) { list ->
                LazyColumn {
                    items(list) { team ->
                        ListItem(
                            modifier = Modifier.clickable { onTeamClick(team) },
                            leadingContent = {
                                AsyncImage(team.badge, null, Modifier.size(48.dp), contentScale = ContentScale.Fit)
                            },
                            headlineContent = { Text(team.strTeam ?: "-") },
                            supportingContent = { Text("${team.strLeague ?: ""} · ${team.strCountry ?: ""}") }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}


@Composable
fun TeamScreen(team: Team, onBack: () -> Unit, onPlayers: () -> Unit, onTable: () -> Unit) {
    AppScaffold(team.strTeam ?: "Equipo", onBack) { mod ->
        Column(
            mod.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(team.badge, null, Modifier.size(140.dp), contentScale = ContentScale.Fit)
            Spacer(Modifier.height(8.dp))
            Text(team.strTeam ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("${team.strLeague ?: ""} · ${team.strCountry ?: ""}")
            team.strStadium?.let { Text("Estadio: $it") }
            team.intFormedYear?.let { Text("Fundado: $it") }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onPlayers, modifier = Modifier.fillMaxWidth()) { Text("Ver jugadores") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onTable, modifier = Modifier.fillMaxWidth()) { Text("Ver tabla de posiciones") }
            team.strDescriptionEN?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Composable
fun PlayersScreen(vm: FootballViewModel, onBack: () -> Unit, onPlayerClick: (Player) -> Unit) {
    AppScaffold("Jugadores", onBack) { mod ->
        Box(mod.fillMaxSize()) {
            StateContent(vm.players) { list ->
                LazyColumn {
                    items(list) { p ->
                        ListItem(
                            modifier = Modifier.clickable { onPlayerClick(p) },
                            leadingContent = {
                                PhotoOrIcon(p.photo, Modifier.size(48.dp), ContentScale.Crop)
                            },
                            headlineContent = { Text(p.strPlayer ?: "-") },
                            supportingContent = { Text("${p.strPosition ?: ""} · ${p.strNationality ?: ""}") }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}


@Composable
fun PlayerScreen(vm: FootballViewModel, onBack: () -> Unit) {
    AppScaffold("Jugador", onBack) { mod ->
        StateContent(vm.selectedPlayer) { p ->
            Column(
                mod.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PhotoOrIcon(p.photo, Modifier.fillMaxWidth().height(320.dp))
                Spacer(Modifier.height(12.dp))
                Text(p.strPlayer ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(p.strTeam ?: "")
                p.strPosition?.let { Text("Posición: $it") }
                p.strNationality?.let { Text("Nacionalidad: $it") }
                p.dateBorn?.let { Text("Nacimiento: $it") }
                p.strHeight?.takeIf { it.isNotBlank() }?.let { Text("Altura: $it") }
                p.strWeight?.takeIf { it.isNotBlank() }?.let { Text("Peso: $it") }
                p.strDescriptionEN?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


@Composable
fun TableScreen(vm: FootballViewModel, onBack: () -> Unit) {
    val myTeamId = vm.selectedTeam?.idTeam
    AppScaffold(vm.selectedTeam?.strLeague ?: "Tabla", onBack) { mod ->
        Box(mod.fillMaxSize()) {
            StateContent(vm.table) { rows ->
                Column {
                    TableHeader()
                    LazyColumn {
                        items(rows) { r -> TableRowItem(r, highlight = r.idTeam == myTeamId) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text("#", Modifier.width(28.dp), fontWeight = FontWeight.Bold)
        Text("Equipo", Modifier.weight(1f), fontWeight = FontWeight.Bold)
        listOf("PJ", "G", "E", "P", "DG", "Pts").forEach {
            Text(it, Modifier.width(34.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
    HorizontalDivider()
}

@Composable
private fun TableRowItem(r: TableRow, highlight: Boolean) {
    val bg = if (highlight) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    Surface(color = bg) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(r.intRank ?: "", Modifier.width(28.dp))
            AsyncImage(r.badge, null, Modifier.size(22.dp))
            Spacer(Modifier.width(6.dp))
            Text(r.strTeam ?: "", Modifier.weight(1f), maxLines = 1, fontSize = 14.sp)
            listOf(r.intPlayed, r.intWin, r.intDraw, r.intLoss, r.intGoalDifference, r.intPoints).forEachIndexed { i, v ->
                Text(
                    v ?: "", Modifier.width(34.dp), textAlign = TextAlign.Center, fontSize = 13.sp,
                    fontWeight = if (i == 5) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
    HorizontalDivider()
}

@Composable
fun PhotoOrIcon(url: String?, modifier: Modifier, contentScale: ContentScale = ContentScale.Fit) {
    val placeholder = rememberVectorPainter(Icons.Default.Person)
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder
    )
}
