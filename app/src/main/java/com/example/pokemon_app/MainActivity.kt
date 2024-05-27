package com.example.pokemon_app

import view_models.PokemonListAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import model.Pokemon
import services.AuthService
import view_models.PokemonViewModel



class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: PokemonViewModel
    private lateinit var pokemonListAdapter: PokemonListAdapter

    private var runnable: Runnable? = null
    private lateinit var handler: Handler
    private lateinit var authService: AuthService


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handler = Handler(Looper.getMainLooper())
        authService = AuthService(this);

       setupRecyclerViewAndViewModel()
        setupEditText()
        setupPopupMenu()
    }

    private fun setupRecyclerViewAndViewModel() {
        recyclerView = findViewById(R.id.pokemonList)
        val progressBar: View = findViewById(R.id.progressBar)

        viewModel = ViewModelProvider(this)[PokemonViewModel::class.java]



        val layoutManager = GridLayoutManager(this,2)

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

        viewModel.pokemonList.observe(this, Observer { pokemonList ->
            pokemonListAdapter.submitList(pokemonList)
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.fetchInitialPokemonList()
    }

    private fun setupEditText() {
        val searchPokemonEditText: EditText = findViewById(R.id.searchPokemonText)

        searchPokemonEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                debounceTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun debounceTextChange(text: String) {
        runnable?.let { handler.removeCallbacks(it) }

        runnable = Runnable {
            val query = text.trim()
            if (query.isNotEmpty()) {
                viewModel.searchPokemonByName(query)
            } else {
                viewModel.fetchInitialPokemonList()
            }
        }
        handler.postDelayed(runnable!!, 500)
    }

    private fun redirectToPokemonDetails(pokemon: Pokemon) {
        val pokemonJson = Gson().toJson(pokemon)

        val intent = Intent(this, PokemonDetailsActivity::class.java).apply {
            putExtra("pokemon", pokemonJson)
        }

        startActivity(intent)
    }

    private fun setupPopupMenu() {
        findViewById<ImageButton>(R.id.popup_menu_button).setOnClickListener {
            if (!authService.isLoggedIn()) {
                val intent = Intent(this, Login::class.java);
                startActivity(intent);
            } else {
                val popup = PopupMenu(this, it)
                val inflater: MenuInflater = popup.menuInflater
                inflater.inflate(R.menu.bottom_nav_menu, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.navigation_home -> {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            true
                        }

                        R.id.navigation_account -> {
                            val intent = Intent(this, AccountActivity::class.java)
                            startActivity(intent)
                            true
                        }

                        R.id.navigation_saved -> {
                            val intent = Intent(this, SavedPokemonsActivity::class.java)
                            startActivity(intent)
                            true
                        }

                        R.id.log_out -> {
                            authService.logoutUser()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            true
                        }

                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
}