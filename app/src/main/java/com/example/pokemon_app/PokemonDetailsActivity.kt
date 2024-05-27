package com.example.pokemon_app

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.google.gson.Gson
import model.FavouritesResults
import model.Pokemon
import model.StatWrapper
import model.TypeWrapper
import services.AuthService
import services.FavouritesService
import utils.PokemonTypeColors
import utils.getColorForStat

class PokemonDetailsActivity : AppCompatActivity() {

    private lateinit var favouritesService: FavouritesService
    private lateinit var authService: AuthService
    private lateinit var _pokemon: Pokemon


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pokemon_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authService = AuthService(this)
        favouritesService = FavouritesService(this)

        val pokemonJson = intent.getStringExtra("pokemon")
        val pokemon = Gson().fromJson(pokemonJson, Pokemon::class.java)

        _pokemon = pokemon

        val pokemonName: TextView = findViewById(R.id.pokemonNameDetails)
        val pokemonImage: ImageView = findViewById(R.id.sprite_image_view_details)

        pokemonName.text = "#" + pokemon.id + " " + pokemon.name.replaceFirstChar { it.uppercase() }
        pokemonImage.load(pokemon.sprites.front_default) {
            transformations(CircleCropTransformation())
        }

        displayPokemonTypes(pokemon.types)
        displayPokemonStats(pokemon.stats)
        setSaveButtonText()
    }

    private fun displayPokemonTypes(types: List<TypeWrapper>) {
        val typesContainer: LinearLayout = findViewById(R.id.typesContainer)

        types.forEach { typeWrapper ->
            val typeName = typeWrapper.type.name.replaceFirstChar { it.uppercase() }
            val typePill = LayoutInflater.from(this).inflate(R.layout.type_pill_view, typesContainer, false) as TextView
            typePill.text = typeName

            val background = typePill.background as GradientDrawable
            background.setColor(PokemonTypeColors.getColorForType(typeWrapper.type.name))

            typesContainer.addView(typePill)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayPokemonStats(stats: List<StatWrapper>) {
        val statsContainer: LinearLayout = findViewById(R.id.stats_container)

        val maxStatValue = stats.maxOf { it.base_stat.toInt() }

        stats.forEach { statWrapper ->
            val statView = layoutInflater.inflate(R.layout.stat_item, statsContainer, false)

            val statNameTextView: TextView = statView.findViewById(R.id.stat_name)
            val statProgressBar: ProgressBar = statView.findViewById(R.id.stat_progress)
            val statValueTextView: TextView = statView.findViewById(R.id.stat_value_text)

            val statName = statWrapper.stat.name.replaceFirstChar { it.uppercase() }
            val statValue = statWrapper.base_stat.toInt()

            val progress = (statValue.toFloat() / maxStatValue.toFloat() * 100).toInt()

            statNameTextView.text = statName
            statProgressBar.progress = progress
            statValueTextView.text = "$statName: $statValue"



            val color = getColorForStat(statWrapper.stat.name, this)
            println(statName)

            val drawable = statProgressBar.progressDrawable.mutate()
            (drawable as? LayerDrawable)?.findDrawableByLayerId(android.R.id.progress)
                ?.setColorFilter(color, PorterDuff.Mode.SRC_IN)

            statsContainer.addView(statView)
        }
    }


    fun togglePokemonSave(www: View) {
        if (!authService.isLoggedIn()) {
            return
        }

        favouritesService.toggleFavourite(FavouritesResults(_pokemon.id.toInt(), _pokemon.name))

        setSaveButtonText()
    }
    @SuppressLint("SetTextI18n")
    fun setSaveButtonText() {
        val isFavourite = favouritesService.isFavouritePokemon(_pokemon.id.toInt())
        val saveButton: Button = findViewById(R.id.savePokemonAsFavourite)

        if (!authService.isLoggedIn()) {
            saveButton.isEnabled = false
        } else {
            saveButton.isEnabled = true
        }

        if (isFavourite) {
            saveButton.text = "-"
            saveButton.setBackgroundColor(Color.parseColor("#e5383b"))
        } else {
            saveButton.text = "+"
            saveButton.setBackgroundColor(Color.parseColor("#669bbc"))
        }
    }

    fun goBack(www: View) {
        finish()
    }
}