package jp.ac.it_college.std.s22026.pokemonapisample.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.jar.Attributes

@Serializable
data class Generation(
    val id: Int,
    val name: String,
    val abilities: List<NamedApiResource>,
    val names: List<Attributes.Name>,
    @SerialName("main_region") val mainRegion: NamedApiResource,
    val moves: NamedApiResource,
    @SerialName("pokemon_species") val pokemonSpecies: List<NamedApiResource>,
    val types: List<NamedApiResource>,
    @SerialName("version_groups") val versionGroups: List<NamedApiResource>,
)