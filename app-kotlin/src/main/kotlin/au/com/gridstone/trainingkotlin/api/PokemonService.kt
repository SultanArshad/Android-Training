package au.com.gridstone.trainingkotlin.api

import au.com.gridstone.trainingkotlin.data.PokemonData
import au.com.gridstone.trainingkotlin.data.PokemonResults
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonService {
  @GET("?limit=151")
  suspend fun getPokemons(): Response<PokemonResults>

  @GET("{pokemonId}")
  suspend fun getPokemonDetails(@Path("pokemonId") pokemonId: String): Response<PokemonData>
}
