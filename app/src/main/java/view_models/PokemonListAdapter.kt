package view_models// PokemonAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.pokemon_app.R
import model.Pokemon
import utils.PokemonTypeColors

class PokemonListAdapter(
    private val loadMoreCallback: () -> Unit,
    private val onCardClickListener: (pokemon: Pokemon) -> Unit,
    private val loading: LiveData<Boolean>
) : ListAdapter<PokemonListItem, RecyclerView.ViewHolder>(DiffCallback) {
    companion object {
        private const val VIEW_TYPE_POKEMON = 0
        private const val VIEW_TYPE_LOAD_MORE = 1

        val DiffCallback = object : DiffUtil.ItemCallback<PokemonListItem>() {
            override fun areItemsTheSame(oldItem: PokemonListItem, newItem: PokemonListItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PokemonListItem, newItem: PokemonListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PokemonListItem.PokemonItem -> VIEW_TYPE_POKEMON
            is PokemonListItem.LoadMoreItem -> VIEW_TYPE_LOAD_MORE
            else -> throw IllegalArgumentException("Invalid item type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_POKEMON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_pokemon, parent, false)
                PokemonViewHolder(view, onCardClickListener)
            }
            VIEW_TYPE_LOAD_MORE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_load_more, parent, false)
                LoadMoreViewHolder(view, loadMoreCallback)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pokemonItem = getItem(position)

        when (holder) {
            is PokemonViewHolder -> {
                val pokemon = (pokemonItem as PokemonListItem.PokemonItem).pokemon
                holder.bind(pokemon)
            }
            is LoadMoreViewHolder -> {
                holder.bind(showLoader = loading)
            }
        }
    }

    class PokemonViewHolder(itemView: View, private val onCardClickListener: (pokemon: Pokemon) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val cardView: View = itemView.findViewById(R.id.pokemonCard)
        private val nameTextView: TextView = itemView.findViewById(R.id.pokemonName)
        private val imageView: ImageView = itemView.findViewById(R.id.sprite_image_view)
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.pokemonCard)

        fun bind(pokemon: Pokemon) {
            cardView.setOnClickListener {
                onCardClickListener(pokemon)
            }

            nameTextView.text = pokemon.name
            imageView.load(pokemon.sprites.front_default) {
                transformations(CircleCropTransformation())
            }

            val typeColor = PokemonTypeColors.getColorForType(pokemon.types[0].type.name)
            val transparentColor = typeColor and 0x80FFFFFF.toInt()
            linearLayout.setBackgroundColor(transparentColor)
        }
    }

    class LoadMoreViewHolder(itemView: View, private val loadMoreCallback: () -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val loadMoreButton: Button = itemView.findViewById(R.id.load_more_button)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.load_more_progress)

        init {
            loadMoreButton.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                loadMoreButton.visibility = View.GONE
                loadMoreCallback()
            }
        }

        fun bind(showLoader: LiveData<Boolean>) {
            showLoader.observe(itemView.context as LifecycleOwner) { isLoading ->
                if (isLoading) {
                    progressBar.visibility = View.VISIBLE
                    loadMoreButton.visibility = View.GONE
                } else {
                    progressBar.visibility = View.GONE
                    loadMoreButton.visibility = View.VISIBLE
                }
            }
        }
    }
}
