package com.example.pokemon_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import model.FavouritesResults
import model.Pokemon
import services.AuthService
import services.FavouritesService
import utils.readFromCsvFile
import utils.readFromTxtFile
import utils.saveToCsvFile
import utils.saveToTxtFile
import view_models.PokemonListAdapter
import view_models.PokemonViewModel

class SavedPokemonsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: PokemonViewModel
    private lateinit var pokemonListAdapter: PokemonListAdapter
    private lateinit var favouriteService: FavouritesService;
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_saved_pokemons)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        favouriteService = FavouritesService(this)
        authService = AuthService(this)

        val backToListButton = findViewById<Button>(R.id.backToListButton)
        backToListButton.setOnClickListener {
            finish()
        }

        setupRecyclerViewAndViewModel()
    }

    override fun onResume() {
        super.onResume()

        favouriteService.getUsersPokemons()

        val pokemonIds = favouriteService.favouritePokemons.map { it.pokemonId }

        viewModel.fetchFavoritePokemonList(pokemonIds)
    }
    private fun setupRecyclerViewAndViewModel() {
        recyclerView = findViewById(R.id.pokemonSavedList)
        val progressBar: View = findViewById(R.id.progressBar);

        viewModel = ViewModelProvider(this)[PokemonViewModel::class.java]

        val layoutManager = GridLayoutManager(this, 2)

        recyclerView.layoutManager = layoutManager

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Span Load more button on two columns
                return if (pokemonListAdapter.getItemViewType(position) == 1) {
                    2
                } else {
                    1
                }
            }
        }

        pokemonListAdapter = PokemonListAdapter({ viewModel.loadMorePokemon() }, { redirectToPokemonDetails(it) }, viewModel.loading)

        recyclerView.adapter = pokemonListAdapter

        viewModel.favouritePokemonList.observe(this, Observer { pokemonList ->
            pokemonListAdapter.submitList(pokemonList)
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        favouriteService.getUsersPokemons()

        val pokemonIds = favouriteService.favouritePokemons.map { it.pokemonId }
        viewModel.fetchFavoritePokemonList(pokemonIds)
    }

    private fun redirectToPokemonDetails(pokemon: Pokemon) {
        val pokemonJson = Gson().toJson(pokemon)

        val intent = Intent(this, PokemonDetailsActivity::class.java).apply {
            putExtra("pokemon", pokemonJson)
        }

        startActivity(intent)
    }

    fun saveAsCSV(www: View) {
        saveToCsvFile(this, authService.getUserId(), favouriteService.favouritePokemons)
        Toast.makeText(this, "Zapisano do pliku .csv", Toast.LENGTH_SHORT).show()
    }

    fun saveAsTxt(www: View) {
        saveToTxtFile(this, authService.getUserId(), favouriteService.favouritePokemons)
        Toast.makeText(this, "Zapisano do pliku .txt", Toast.LENGTH_SHORT).show()
    }

    fun importAsCSV(www: View) {
        try {
            val pokemons = readFromCsvFile(this, authService.getUserId())

            onImport(pokemons)

            Toast.makeText(this, "Poprawnie zaimportowano z pliku .csv", Toast.LENGTH_SHORT).show()
        } catch (error: Error) {
            Toast.makeText(this, "Wystąpił problem przy imporcie. Plik prawdopodobnie nie istnieje.", Toast.LENGTH_SHORT).show()
        }
    }

    fun importAsTxt(www: View) {
        try {
            val pokemons = readFromTxtFile(this, authService.getUserId())

            onImport(pokemons)

            Toast.makeText(this, "Poprawnie zaimportowano z pliku .txt", Toast.LENGTH_SHORT).show()
        } catch (error: Error) {
            Toast.makeText(this, "Wystąpił problem przy imporcie. Plik prawdopodobnie nie istnieje.", Toast.LENGTH_SHORT).show()
        }
    }

  private fun onImport(pokemons: List<FavouritesResults>) {
        favouriteService.importToDatabase(pokemons)

        favouriteService.getUsersPokemons()

        val pokemonIds = favouriteService.favouritePokemons.map { it.pokemonId }

        viewModel.fetchFavoritePokemonList(pokemonIds)
    }
}