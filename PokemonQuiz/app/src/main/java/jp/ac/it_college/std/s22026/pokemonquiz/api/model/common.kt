package jp.ac.it_college.std.s22026.pokemonquiz.api.model

import kotlinx.serialization.Serializable
import org.intellij.lang.annotations.Language

@Serializable
data class NamedAPIResource(
    val name: String,
    val url: String,
)

@Serializable
data class Name(
    val name: String,
    val language: NamedAPIResource
)