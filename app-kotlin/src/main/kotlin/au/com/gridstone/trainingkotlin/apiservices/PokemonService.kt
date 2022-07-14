package au.com.gridstone.trainingkotlin.apiservices

import au.com.gridstone.trainingkotlin.data.PokemonResults
import retrofit2.Response
import retrofit2.http.GET

interface  PokemonService {
  @GET("?limit=151")
  suspend fun getPokemons(): Response<PokemonResults>
}
