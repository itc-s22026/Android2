package jp.ac.it_college.std.s22026.pokemonquiz.model

data class PokeQuiz(
    val imageUrl: String,
    val choices: List<String>,
    val correct: String
)