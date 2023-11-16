package jp.ac.it_college.std.s22026.pokemonquiz

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import jp.ac.it_college.std.s22026.pokemonquiz.generation.SelectGenerationScene
import jp.ac.it_college.std.s22026.pokemonquiz.model.PokeQuiz
import jp.ac.it_college.std.s22026.pokemonquiz.quiz.QuizScene
import jp.ac.it_college.std.s22026.pokemonquiz.result.ResultScene
import jp.ac.it_college.std.s22026.pokemonquiz.title.TitleScene

object Destinations {
    const val TITLE = "title"
    const val GENERATION = "generation_select"
    const val QUIZ = "quiz/{order}"
    const val RESULT = "result"
}
@OptIn(ExperimentalMaterial3Api::class)     // 比較的新しい実験的な機能を使うときに指定するらしい。
@Composable
fun PokeNavigation(
    navController: NavHostController = rememberNavController(),     // ナビゲーションの指示出すやつ
) {
    // AppBar の文言を保持するやつ
    var titleText by remember { mutableStateOf("") }
    var quizData by remember { mutableStateOf(listOf<PokeQuiz>()) }

    // Scaffold を使うと、NavHost 以外の部分の構築を手っ取り早くできる。
    Scaffold(
        // 上部のバー
        topBar = {
            TopAppBar(title = {
                Text(text = titleText)
            })
        }
    ) {
        // メインコンテンツ部分
        // startDestination は初期表示デスティネーション
        // modifier で余白設定をする。Scaffold からもらえる PaddingValues[it] を使う
        NavHost(
            navController = navController,
            startDestination = Destinations.TITLE,
            modifier = Modifier.padding(it)
        ) {
            // composable関数でデスティネーションを登録していく。
            composable(Destinations.TITLE) {
                // タイトル画面
                titleText = ""
                TitleScene(
                    onTitleClick = {
                        navController.navigate(Destinations.GENERATION)
                    }
                )
            }

            composable(Destinations.GENERATION) {
                // 世代選択画面
                titleText = stringResource(id = R.string.generation)
                SelectGenerationScene(onGenerationSelected = { gen ->
                    quizData = generateQuizData(gen)
                    navController.navigate("quiz/0")
                })
            }

            composable(
                Destinations.QUIZ,
                // [arguments] パラメータに前のデスティネーションから受け取るデータを定義する
                arguments = listOf(navArgument("order") { type = NavType.IntType })
            ) {
                titleText = ""
                // composable#arguments の定義に従って取得できるようになる。非タイプセーフ
                val order = it.arguments?.getInt("order") ?: 0
                QuizScene(quizData[order])
            }

            composable(Destinations.RESULT) {
                ResultScene(result = 0)
            }
        }
    }
}

fun generateQuizData(generation: Int): List<PokeQuiz> {
    // 今はダミーデータの状態。
    // 後々はちゃんと生成する予定
    return listOf(
        PokeQuiz(
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/906.png",
            // 選択肢のシャッフルは QuizScene 表示前に実施すること。
            // QuizScene にデータを渡したあとだと、再コンポーズ時に毎回シャッフルされる
            choices = listOf("ニャオハ", "ホゲータ", "クワッス", "グルトン").shuffled(),
            correct = "ニャオハ"
        ),
    )
}