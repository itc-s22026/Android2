package jp.ac.it_college.std.s22026.pokemonquiz.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Ktor Client オブジェクトを管理しつつ
 * PokeAPI の各エンドポイントで共通の部分を実装
 */
object Client {
    /** ベースURL */
    private const val BASE_URL = "https://pokeapi.co/api/v2"
    /** Ktor Client */
    private val ktor = HttpClient(CIO) {
        engine {
            endpoint {
                connectTimeout = 5000
                requestTimeout = 5000
                socketTimeout = 5000
            }
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }

    suspend fun get(endpoint: String) =
        ktor.get { url("$BASE_URL$endpoint") }
}