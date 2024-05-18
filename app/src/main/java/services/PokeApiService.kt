package services
import clients.ApiClient
import model.Pokemon
import model.PokemonListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PokeApiService {
    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int = 20, @Query("offset") offset: Int = 0): Call<PokemonListResponse>

    @GET
    fun getPokemonListFromUrl(@Url url: String): Call<PokemonListResponse>

    @GET
    fun getPokemonDetails(@Url url: String): Call<Pokemon>

    companion object {
        fun create(): PokeApiService {
            return ApiClient.retrofit.create(PokeApiService::class.java)
        }
    }
}