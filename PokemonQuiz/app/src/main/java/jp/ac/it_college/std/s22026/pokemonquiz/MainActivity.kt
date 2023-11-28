package jp.ac.it_college.std.s22026.pokemonquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import jp.ac.it_college.std.s22026.pokemonquiz.ui.theme.PokemonQuizTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PokemonQuizTheme {
                PokeNavigation()
            }
        }
    }
}



