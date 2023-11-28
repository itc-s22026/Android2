package jp.ac.it_college.std.s22026.pokemonapisample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.ac.it_college.std.s22026.pokemonapisample.api.Games
import jp.ac.it_college.std.s22026.pokemonapisample.ui.theme.PokemonAPISampleTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonAPISampleTheme {
                MainScene(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun MainScene(modifier: Modifier = Modifier) {
    var resultText by remember {
        mutableStateOf("結果表示")
    }
    // Composable な関数内でコルーチンを使用するためのコルーチンスコープ
    val scope = rememberCoroutineScope()

    Surface(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedButton(
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = {
                    scope.launch {
                        resultText = Games.getGenerations().toString()
                    }
                }
            ) {
                Text(text = "API Test")
            }
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Text(text = resultText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScenePreview() {
    PokemonAPISampleTheme {
        MainScene(Modifier.fillMaxSize())
    }
}