package model

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListResult>
)

data class PokemonListResult(
    val name: String,
    val url: String,
)

data class Pokemon(
    val id: Number,
    val name: String,
    val url: String,
    val sprites: Sprites
)

data class Sprites(
    val front_default: String?,
    val back_default: String?,
    val front_shiny: String?,
    val back_shiny: String?
)
