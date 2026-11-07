package com.example.futbolera.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SportsApi {
    @GET("searchteams.php")
    suspend fun searchTeams(@Query("t") name: String): TeamsResponse

    @GET("lookup_all_players.php")
    suspend fun players(@Query("id") teamId: String): PlayersResponse

    @GET("lookuptable.php")
    suspend fun table(@Query("l") leagueId: String): TableResponse

    @GET("lookupplayer.php")
    suspend fun player(@Query("id") playerId: String): PlayerDetailResponse
}

object ApiClient {
    private const val BASE_URL = "https://www.thesportsdb.com/api/v1/json/123/"

    val api: SportsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SportsApi::class.java)
    }
}
