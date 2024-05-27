package com.example.pokemon_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import services.AuthService
import services.FavouritesService

class AccountActivity : AppCompatActivity() {

    private lateinit var authService: AuthService
    private lateinit var favouritesService: FavouritesService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authService = AuthService(this);
        favouritesService = FavouritesService(this)

        setupView()
    }

    fun goBack(www: View) {
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {
        val idTextView: TextView = findViewById(R.id.account_details_id)
        val usernameView: TextView = findViewById(R.id.account_details_name)
        val pokemonsCountView: TextView = findViewById(R.id.account_details_saved_pokemons_count)

        idTextView.text = "User ID: " + authService.getUserId().toString()
        usernameView.text = "Username: " + authService.getUserInfo()?.username
        pokemonsCountView.text = "Saved pokemons count: " + favouritesService.favouritePokemons.count().toString()
    }

    fun deleteAccount(www: View) {
        val success = authService.deleteAccount()

        if (success) {
            Toast.makeText(this, "Konto zostało usunięte", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}