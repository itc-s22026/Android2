package jp.ac.it_college.std.s22026.pokemonquiz

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import jp.ac.it_college.std.s22026.pokemonquiz.database.PokeRoomDatabase
import jp.ac.it_college.std.s22026.pokemonquiz.database.emtity.Poke
import jp.ac.it_college.std.s22026.pokemonquiz.ui.theme.PokemonQuizTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Composable 内で使えるコルーチンスコープ
            val scope = rememberCoroutineScope()
            // 現在の処理を実施している Context 取得
            val context = LocalContext.current

            // テストのダミーデータ登録
            scope.launch {
                testDataIntialzeDatabase(context)
            }
            PokemonQuizTheme {
                PokeNavigation()
            }
        }
    }
}

private suspend fun testDataIntialzeDatabase(context: Context) {
    withContext(Dispatchers.IO) {
        val dao = PokeRoomDatabase.getDatabase(context).pokeDao()
        // もし既にダミーデータが入っていれば何もしないで終了する
        if (dao.findByGeneration(9).isNotEmpty()) return@withContext

        // 以下はダミーデータの登録
        dao.insertAll(
            listOf(
                Poke(906,
                    9,
                    "にゃハオ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/906.png"
                ),
                Poke(909,
                    9,
                    "ホゲータ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/909.png"
                ),
                Poke(912,
                    9,
                    "クワッス",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/912.png"
                ),
                Poke(915,
                    9,
                    "グルトン",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/915.png"
                ),
                Poke(921,
                    9,
                    "パモ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/921.png"
                ),
                Poke(924,
                    9,
                    "ワッカネズミ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/924.png"
                ),
                Poke(926,
                    9,
                    "パピモッチ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/926.png"
                ),
                Poke(928,
                    9,
                    "ミニープ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/928.png"
                ),
                Poke(931,
                    9,
                    "イキリンコ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/931.png"
                ),
                Poke(932,
                    9,
                    "コジオ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/932.png"
                ),
                Poke(
                    935,
                    9,
                    "カルボウ",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/935.png"
                )
            )
        )
    }
}


