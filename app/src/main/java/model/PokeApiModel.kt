package model
import android.os.Parcel
import android.os.Parcelable

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListResult>
)

data class PokemonListResult(
    val name: String,
    val url: String,
)

data class Pokemon(
    val id: Number,
    val name: String,
    val url: String,
    val sprites: Sprites,
    val abilities: List<AbilityWrapper>,
    val baseExperience: Number,
    val moves: List<MoveWrapper>,
    val stats: List<StatWrapper>,
    val types: List<TypeWrapper>,
    val weight: Number
)


data class Sprites(
    val front_default: String?,
    val back_default: String?,
    val front_shiny: String?,
    val back_shiny: String?
)

data class AbilityWrapper(
    val ability: Ability,
    val is_hidden: Boolean,
    val slot: Number
)

data class Ability(
    val name: String,
    val url: String
)

data class MoveWrapper(
   val move: Move
)

data class Move(
    val name: String,
    val url: String
)

data class StatWrapper(
    val base_stat: Number,
    val effort: Number,
    val stat: Stat
)

data class Stat(
  val name: String,
  val url: String
)

data class TypeWrapper(
    val slot: Number,
    val type: Type
)

data class Type(
    val name: String,
    val url: String
)