package services

import android.content.Context
import database.DatabaseHelper
import database.FavouritesResults

class FavouritesService(context: Context) {

   private val authService = AuthService(context)
    private val dbHelper = DatabaseHelper(context)
    var favouritePokemons: List<FavouritesResults> = listOf()

    init {
        getUsersPokemons()
    }

    fun getUsersPokemons() {
        if (!authService.isLoggedIn()) favouritePokemons =  emptyList();

        favouritePokemons = dbHelper.getFavorites(authService.getUserId())
    }

     fun isFavouritePokemon(pokemonId: Int): Boolean {
       return favouritePokemons.any {it.pokemonId == pokemonId }
    }

    fun toggleFavourite(pokemon: FavouritesResults) {
        if (!authService.isLoggedIn()) {
            return
        }

        val userId = authService.getUserId()

         if (!isFavouritePokemon(pokemon.pokemonId)) {
            dbHelper.addFavorite(userId, pokemon)
        } else {
            dbHelper.removeFavorite(userId, pokemon.pokemonId)
        }

        getUsersPokemons()
    }

    fun importToDatabase(pokemons: List<FavouritesResults>) {
        val userId = authService.getUserId()

        for (pokemon in pokemons) {
            dbHelper.addFavorite(userId, pokemon)
        }
    }
}