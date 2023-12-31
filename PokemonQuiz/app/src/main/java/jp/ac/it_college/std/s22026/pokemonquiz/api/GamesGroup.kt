package jp.ac.it_college.std.s22026.pokemonquiz.api

import io.ktor.client.call.body
import jp.ac.it_college.std.s22026.pokemonquiz.api.model.Generation

object GamesGroup {
    /**
     * 世代情報を取る
     */
    suspend fun getGeneration(gen: Int = 0): Generation {
        return Client.get("/generation/${gen}/").body()
    }
}