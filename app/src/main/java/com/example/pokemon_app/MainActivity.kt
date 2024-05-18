package com.example.pokemon_app

import PokemonListAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import view_models.PokemonViewModel



class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: PokemonViewModel
    private lateinit var pokemonListAdapter: PokemonListAdapter

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

        recyclerView = findViewById(R.id.pokemonList)
        val progressBar: View = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        pokemonListAdapter = PokemonListAdapter()
        recyclerView.adapter = pokemonListAdapter


        viewModel = ViewModelProvider(this)[PokemonViewModel::class.java]

        viewModel.pokemonList.observe(this, Observer { pokemonList ->
             pokemonListAdapter.submitList(pokemonList)
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if(isLoading) View.GONE else View.VISIBLE
        })

        viewModel.fetchPokemonList()
    }
}