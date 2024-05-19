package utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.pokemon_app.R

object PokemonTypeColors {
    private val typeColors = mapOf(
        "normal" to Color.parseColor("#A8A77A"),
        "fire" to Color.parseColor("#EE8130"),
        "water" to Color.parseColor("#6390F0"),
        "electric" to Color.parseColor("#F7D02C"),
        "grass" to Color.parseColor("#7AC74C"),
        "ice" to Color.parseColor("#96D9D6"),
        "fighting" to Color.parseColor("#C22E28"),
        "poison" to Color.parseColor("#A33EA1"),
        "ground" to Color.parseColor("#E2BF65"),
        "flying" to Color.parseColor("#A98FF3"),
        "psychic" to Color.parseColor("#F95587"),
        "bug" to Color.parseColor("#A6B91A"),
        "rock" to Color.parseColor("#B6A136"),
        "ghost" to Color.parseColor("#735797"),
        "dragon" to Color.parseColor("#6F35FC"),
        "dark" to Color.parseColor("#705746"),
        "steel" to Color.parseColor("#B7B7CE"),
        "fairy" to Color.parseColor("#D685AD")
    )

    fun getColorForType(type: String): Int {
        return typeColors[type] ?: Color.GRAY
    }
}

fun getColorForStat(statName: String, context: Context): Int {
    return when (statName) {
        "hp" -> ContextCompat.getColor(context, R.color.stat_hp)
        "attack" -> ContextCompat.getColor(context, R.color.stat_attack)
        "defense" -> ContextCompat.getColor(context, R.color.stat_defense)
        "special-attack" -> ContextCompat.getColor(context, R.color.stat_special_attack)
        "special-defense" -> ContextCompat.getColor(context, R.color.stat_special_defense)
        "speed" -> ContextCompat.getColor(context, R.color.stat_speed)
        else -> Color.GRAY
    }
}
