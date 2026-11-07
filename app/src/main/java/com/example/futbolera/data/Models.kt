package com.example.futbolera.data

data class TeamsResponse(val teams: List<Team>?)

data class Team(
    val idTeam: String?,
    val strTeam: String?,
    val idLeague: String?,
    val strLeague: String?,
    val strStadium: String?,
    val strCountry: String?,
    val intFormedYear: String?,
    val strDescriptionEN: String?,
    val strBadge: String?,
    val strTeamBadge: String?
) {
    val badge: String? get() = strBadge ?: strTeamBadge
}

data class PlayersResponse(val player: List<Player>?)

data class PlayerDetailResponse(val players: List<Player>?)

data class Player(
    val idPlayer: String?,
    val strPlayer: String?,
    val strTeam: String?,
    val strPosition: String?,
    val strNationality: String?,
    val dateBorn: String?,
    val strHeight: String?,
    val strWeight: String?,
    val strThumb: String?,
    val strCutout: String?,
    val strRender: String?,
    val strDescriptionEN: String?
) {
    val photo: String? get() = strCutout ?: strThumb ?: strRender
}

data class TableResponse(val table: List<TableRow>?)

data class TableRow(
    val idTeam: String?,
    val strTeam: String?,
    val strBadge: String?,
    val strTeamBadge: String?,
    val intRank: String?,
    val intPlayed: String?,
    val intWin: String?,
    val intDraw: String?,
    val intLoss: String?,
    val intGoalsFor: String?,
    val intGoalsAgainst: String?,
    val intGoalDifference: String?,
    val intPoints: String?
) {
    val badge: String? get() = strBadge ?: strTeamBadge
}
