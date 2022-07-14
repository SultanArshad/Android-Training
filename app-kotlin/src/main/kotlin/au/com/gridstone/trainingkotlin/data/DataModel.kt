package au.com.gridstone.trainingkotlin.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PokemonResults(val results: List<Pokemon>)

@Parcelize
data class Pokemon(
  val url: String,
  val name: String,
  val id: String = url.split("/").dropLast(1).last(),
) : Parcelable
