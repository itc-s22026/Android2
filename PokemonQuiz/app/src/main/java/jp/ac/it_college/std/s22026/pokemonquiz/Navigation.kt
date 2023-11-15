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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.ac.it_college.std.s22026.pokemonquiz.generation.SelectGenerationScene
import jp.ac.it_college.std.s22026.pokemonquiz.quiz.QuizScene
import jp.ac.it_college.std.s22026.pokemonquiz.result.ResultScene
import jp.ac.it_college.std.s22026.pokemonquiz.title.TitleScene

object Destinations {
    const val TITLE = "title"
    const val GENERATION = "generation_select"
    const val QUIZ = "quiz/{order}"
    const val RESULT = "result"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeNavigation(
    navController: NavHostController = rememberNavController(),
) {
    var titleText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = titleText)
            })
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Destinations.TITLE,
            modifier = Modifier.padding(it)
        ){
            composable(Destinations.TITLE) {
                TitleScene(
                    onTitleClick = {
                        navController.navigate(Destinations.GENERATION)
                    }
                )
            }
            composable(Destinations.GENERATION) {
                titleText = stringResource(id = R.string.plaease_select_pokemon)
                SelectGenerationScene()
            }
            composable(Destinations.QUIZ) {
                QuizScene(imageUrl = "", choices = listOf())
            }
            composable(Destinations.RESULT) {
                ResultScene(result = 0)
            }
        }
    }
}