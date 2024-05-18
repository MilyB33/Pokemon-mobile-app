package com.example.pokemon_app

import PokemonListAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import view_models.PokemonViewModel



class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: PokemonViewModel
    private lateinit var pokemonListAdapter: PokemonListAdapter

    private var runnable: Runnable? = null
    private lateinit var handler: Handler


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

       setupRecyclerViewAndViewModel()
        setupEditText()
    }

    private fun setupRecyclerViewAndViewModel() {
        recyclerView = findViewById(R.id.pokemonList)
        val progressBar: View = findViewById(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        pokemonListAdapter = PokemonListAdapter()
        recyclerView.adapter = pokemonListAdapter

        viewModel = ViewModelProvider(this)[PokemonViewModel::class.java]

        viewModel.pokemonList.observe(this, Observer { pokemonList ->
            pokemonListAdapter.submitList(pokemonList)
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.fetchPokemonList()

        // Load more when scrolling to the bottom
        viewModel.loading.observe(this, Observer { isLoading ->
            if (isLoading == false) {
                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (!recyclerView.canScrollVertically(1)) {
                            viewModel.loadMore()
                        }
                    }
                })
            }
        })
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
                viewModel.fetchPokemonList()
            }
        }
        handler.postDelayed(runnable!!, 500)
    }
}