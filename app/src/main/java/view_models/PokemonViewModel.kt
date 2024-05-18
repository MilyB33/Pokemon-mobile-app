package view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import model.Pokemon
import model.PokemonListResponse
import services.PokeApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PokemonViewModel: ViewModel() {
    private val _pokemonList = MutableLiveData<List<Pokemon>>()
    val pokemonList: LiveData<List<Pokemon>> get () = _pokemonList

    private val _nextUrl = MutableLiveData<String?>()
    val nextUrl: LiveData<String?> get() = _nextUrl

    private val service = PokeApiService.create()

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        fetchPokemonData()
    }

    fun fetchPokemonData() {
        service.getPokemonList().enqueue(object: Callback<PokemonListResponse> {
            override fun onResponse(call: Call<PokemonListResponse>, response: Response<PokemonListResponse>) {
                if (response.isSuccessful) {
                    _pokemonList.value = response.body()?.results ?: emptyList()
                }
            }

            override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}