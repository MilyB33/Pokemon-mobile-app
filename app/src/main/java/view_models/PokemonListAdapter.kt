// PokemonAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.pokemon_app.R
import model.Pokemon
import utils.PokemonTypeColors

class PokemonListAdapter : ListAdapter<Pokemon, PokemonListAdapter.PokemonViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position)
        holder.bind(pokemon)
    }

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.pokemonName)
        private val imageView: ImageView = itemView.findViewById(R.id.sprite_image_view)
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.pokemonCard)

        fun bind(pokemon: Pokemon) {
            nameTextView.text = pokemon.name
            imageView.load(pokemon.sprites.front_default) {
                transformations(CircleCropTransformation())
            }

            val typeColor = PokemonTypeColors.getColorForType(pokemon.types[0].type.name)
            val transparentColor = typeColor and 0x80FFFFFF.toInt()
            linearLayout.setBackgroundColor(transparentColor)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Pokemon>() {
        override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
            return oldItem == newItem
        }
    }
}
