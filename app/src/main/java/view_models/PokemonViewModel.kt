package view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Pokemon
import model.PokemonListResponse
import services.PokeApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicInteger


class PokemonViewModel: ViewModel() {
    private val _pokemonList = MutableLiveData<List<Pokemon>>()
    val pokemonList: LiveData<List<Pokemon>> get () = _pokemonList

    private val _nextUrl = MutableLiveData<String?>()
    val nextUrl: LiveData<String?> get() = _nextUrl

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading


    private val service = PokeApiService.create()

    fun fetchPokemonList(url: String? = null) {
        _loading.value = true
        val call = if (url == null) {
            service.getPokemonList()
        } else {
            service.getPokemonListFromUrl(url)
        }

        call.enqueue(object : Callback<PokemonListResponse> {
            override fun onResponse(call: Call<PokemonListResponse>, response: Response<PokemonListResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { listResponse ->
                        _nextUrl.postValue(listResponse.next)

                        val detailedPokemonList = mutableListOf<Pokemon>()
                        val remainingCount = AtomicInteger(listResponse.results.size)

                        listResponse.results.forEach { result ->
                            val detailCall = service.getPokemonDetails(result.url)
                            detailCall.enqueue(object : Callback<Pokemon> {
                                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                                    if (response.isSuccessful) {
                                        response.body()?.let { pokemon ->
                                            detailedPokemonList.add(pokemon)
                                        }
                                    }
                                    // Check if all Pokémon details are fetched
                                    if (remainingCount.decrementAndGet() == 0) {
                                        // Sort the list by IDs
                                        val sortedPokemonList = detailedPokemonList.sortedWith(compareBy { (it.id.toString().toInt()) })
                                        // Post the sorted list to LiveData
                                        _pokemonList.postValue(sortedPokemonList)
                                        _loading.postValue(false)
                                    }
                                }

                                override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                                    // Handle the failure
                                    // Decrement the count regardless of failure to avoid infinite loop
                                    if (remainingCount.decrementAndGet() == 0) {
                                        // Sort the list by IDs
                                        val sortedPokemonList = detailedPokemonList.sortedWith(compareBy { (it.id.toString().toInt()) })
                                        // Post the sorted list to LiveData
                                        _pokemonList.postValue(sortedPokemonList)
                                        _loading.postValue(false)
                                    }
                                }
                            })
                        }
                    }
                } else {
                    _loading.postValue(false)
                    _pokemonList.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                _loading.postValue(false)
                _pokemonList.postValue(emptyList())
            }
        })
    }


    fun loadMore() {
        _nextUrl.value?.let { url ->
            fetchPokemonList(url)
        }
    }

}