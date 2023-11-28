package jp.ac.it_college.std.s22026.pokemonquiz.download

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.ac.it_college.std.s22026.pokemonquiz.R
import jp.ac.it_college.std.s22026.pokemonquiz.ui.theme.PokemonQuizTheme

@Composable
fun Download(modifier: Modifier = Modifier) {
    Surface(modifier) {
        Column {
            Text(text = stringResource(id = R.string.loading))
            LinearProgressIndicator()
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DownloadPreview() {
    PokemonQuizTheme {
        Download(Modifier.fillMaxSize())
    }
}