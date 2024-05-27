package view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Pokemon
import model.PokemonListResponse
import model.PokemonListResult
import services.PokeApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicInteger


class PokemonViewModel : ViewModel() {
    private val _pokemonList = MutableLiveData<List<PokemonListItem>>()
    private val _favouritePokemonList = MutableLiveData<List<PokemonListItem>>()

    val pokemonList: LiveData<List<PokemonListItem>> get() = _pokemonList
    val favouritePokemonList: LiveData<List<PokemonListItem>> get() = _favouritePokemonList

    private val _nextUrl = MutableLiveData<String?>()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val service = PokeApiService.create()

    fun fetchInitialPokemonList() {
        _loading.value = true
        service.getPokemonList().enqueue(object : Callback<PokemonListResponse> {
            override fun onResponse(
                call: Call<PokemonListResponse>,
                response: Response<PokemonListResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { listResponse ->
                        _nextUrl.postValue(listResponse.next)
                        fetchDetailedPokemonList(listResponse.results, replaceList = true)
                    } ?: run {
                        _pokemonList.postValue(emptyList())
                        _loading.postValue(false)
                    }
                } else {
                    _pokemonList.postValue(emptyList())
                    _loading.postValue(false)
                }
            }

            override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                _pokemonList.postValue(emptyList())
                _loading.postValue(false)
            }
        })
    }

    fun loadMorePokemon() {
        _nextUrl.value?.let { url ->
            _loading.value = true
            service.getPokemonListFromUrl(url).enqueue(object : Callback<PokemonListResponse> {
                override fun onResponse(
                    call: Call<PokemonListResponse>,
                    response: Response<PokemonListResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { listResponse ->
                            _nextUrl.postValue(listResponse.next)
                            fetchDetailedPokemonList(listResponse.results, replaceList = false)
                        } ?: run {
                            _loading.postValue(false)
                        }
                    } else {
                        _loading.postValue(false)
                    }

                    _loading.postValue(false)
                }

                override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                    _loading.postValue(false)
                }
            })
        }
    }

    private fun fetchDetailedPokemonList(results: List<PokemonListResult>, replaceList: Boolean) {
        val detailedPokemonList = mutableListOf<Pokemon>()
        val remainingCount = AtomicInteger(results.size)

        results.forEach { result ->
            service.getPokemonDetails(result.url).enqueue(object : Callback<Pokemon> {
                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                    if (response.isSuccessful) {
                        response.body()?.let { pokemon ->
                            detailedPokemonList.add(pokemon)
                        }
                    }
                    if (remainingCount.decrementAndGet() == 0) {
                        updatePokemonList(detailedPokemonList, replaceList)
                    }
                }

                override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                    if (remainingCount.decrementAndGet() == 0) {
                        updatePokemonList(detailedPokemonList, replaceList)
                    }
                }
            })
        }
    }

    private fun updatePokemonList(newPokemons: List<Pokemon>, replaceList: Boolean) {
        val currentList =
            _pokemonList.value.orEmpty().filterIsInstance<PokemonListItem.PokemonItem>()
                .map { it.pokemon }
        val updatedList = if (replaceList) {
            newPokemons.sortedBy { (it.id.toString().toInt()) }
        } else {
            (currentList + newPokemons).sortedBy { (it.id.toString().toInt()) }
        }
        val listItems: MutableList<PokemonListItem> =
            updatedList.map { PokemonListItem.PokemonItem(it) }.toMutableList()
        if (_nextUrl.value != null) {
            listItems.add(PokemonListItem.LoadMoreItem)
        }
        _pokemonList.postValue(listItems)
        _loading.postValue(false)
    }

    fun searchPokemonByName(name: String) {
        _nextUrl.value = null
        _loading.value = true
        val call = service.getPokemonByName(name)

        call.enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    _pokemonList.postValue(pokemon?.let { listOf(PokemonListItem.PokemonItem(it)) } ?: emptyList())
                } else {
                    _pokemonList.postValue(emptyList())
                }
                _loading.postValue(false)
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                _pokemonList.postValue(emptyList())
                _loading.postValue(false)
            }
        })
    }

    fun fetchFavoritePokemonList(ids: List<Int>) {
        if (ids.isEmpty()) {
            _loading.value = false
            _favouritePokemonList.value = emptyList()
            return
        }

        _loading.value = true
        val callList = ids.map { service.getPokemonByName(it.toString()) }
        val fetchedFavoritePokemonList = mutableListOf<Pokemon>()

        callList.forEach { call ->
            call.enqueue(object : Callback<Pokemon> {
                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                    if (response.isSuccessful) {
                        response.body()?.let { pokemon ->
                            fetchedFavoritePokemonList.add(pokemon)
                        }
                    }
                    checkIfAllFavoritePokemonFetched(ids.size, fetchedFavoritePokemonList)
                }

                override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                    checkIfAllFavoritePokemonFetched(ids.size, fetchedFavoritePokemonList)
                }
            })
        }
    }


    private fun checkIfAllFavoritePokemonFetched(expectedCount: Int, fetchedFavoritePokemonList: List<Pokemon>) {
        if (fetchedFavoritePokemonList.size == expectedCount) {
            val listItems: MutableList<PokemonListItem> = fetchedFavoritePokemonList.map { PokemonListItem.PokemonItem(it) }.toMutableList()
            _favouritePokemonList.postValue(listItems)
            _loading.postValue(false)
        }
    }
}

sealed class PokemonListItem {
    data class PokemonItem(val pokemon: Pokemon) : PokemonListItem()
    data object LoadMoreItem : PokemonListItem()
}


