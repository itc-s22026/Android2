package jp.ac.it_college.std.s22026.pokemonquiz.download

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jp.ac.it_college.std.s22026.pokemonquiz.api.GamesGroup
import jp.ac.it_college.std.s22026.pokemonquiz.api.PokemonGroup
import jp.ac.it_college.std.s22026.pokemonquiz.dataStore
import jp.ac.it_college.std.s22026.pokemonquiz.database.PokeRoomDatabase
import jp.ac.it_college.std.s22026.pokemonquiz.database.emtity.Poke
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


suspend fun pokeDataDownload(context: Context, scope: CoroutineScope, onFinished: () -> Unit) {
    withContext(Dispatchers.IO) {
        runBlocking {
            // PokeAPI にアクセスしてデータを取ってくる
            val data = pokeApi()

            // Daoとる
            val dao = PokeRoomDatabase.getDatabase(context).pokeDao()
            // データ全消し
            dao.deleteByGeneration(9)
            // データベースに登録
            dao.insertAll(data)

            // 更新日時を保存
            val LAST_UPDATED_AT = stringPreferencesKey("last_updated_at")
            context.dataStore.edit {
                it[LAST_UPDATED_AT] = LocalDateTime.now().toString()
            }
        }
    }
    // 終わった報告
    onFinished()
}

suspend fun pokeApi(): List<Poke> {
    // Generation 取る(とりあえず第9世代だけ)
    val generation = GamesGroup.getGeneration(9)

    // pokemonSpecies 分のデータを取ってくる
    val species = generation.pokemonSpecies.map {
        PokemonGroup.getPokemonSpecies(it)
    }

    // species から日本語の名前とIDだけのリストを作る
    val nameList = species.associate {
        val ja = it.names.find { it.language.name == "ja-Hrkt" }?.name ?: ""
        it.id to ja
    }

    // species 分のデータを取ってくる
    val pokemon = species.mapNotNull {
        try {
            PokemonGroup.getPokemon(it.varieties[0].pokemon)
        } catch (e: Exception) {
            Log.w("PokeAPI", "Pokemonの取得で問題が発生しました(id: ${it.id})", e)
            null
        }
    }

    // Poke エンティティに変換
    val dataset = pokemon.map {
        Poke(
            it.id.toLong(),
            9,
            nameList[it.id] ?: throw IllegalStateException("名前ないけど"),
            it.sprites.other.officialArtwork.frontDefault
        )
    }
    return dataset
}