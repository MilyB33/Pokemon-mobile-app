package model

data class User(val id: Int, val username: String, val password: String)

data class FavouritesResults(val pokemonId: Int, val pokemonName: String)