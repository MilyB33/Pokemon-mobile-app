package view_models

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon_app.R
import model.Pokemon
import coil.load

class PokemonListAdapter(private val pokemonList: List<Pokemon>): RecyclerView.Adapter<PokemonListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = itemView.findViewById(R.id.pokemonName)
        private val spriteImageView: ImageView = itemView.findViewById(R.id.sprite_image_view)


        fun bind(pokemon: Pokemon) {
            try {
                nameTextView.text = pokemon.name

//                if (pokemon.sprites.front_default?.isNotEmpty() == true) {
//                    spriteImageView.load(pokemon.sprites.front_default)
//                }
            } catch (error: Exception) {
                error.message?.let { Log.e("PokemonListAdapter", it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.bind(pokemon)
    }

    override fun getItemCount() = pokemonList.size
}