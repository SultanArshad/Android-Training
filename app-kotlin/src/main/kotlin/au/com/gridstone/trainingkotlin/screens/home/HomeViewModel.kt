package au.com.gridstone.trainingkotlin.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.gridstone.trainingkotlin.api.PokemonService
import au.com.gridstone.trainingkotlin.data.PokemonResults
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Loading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import retrofit2.Response

sealed class HomeViewState {
  object Loading : HomeViewState()
  object Error : HomeViewState()
  data class Success(val results: PokemonResults) : HomeViewState()
}

class HomeViewModel() : ViewModel(), KoinComponent {
  private val stateFlow: MutableStateFlow<HomeViewState> = MutableStateFlow(Loading)
  val state: StateFlow<HomeViewState> = stateFlow
  private val webservice: PokemonService = get()

  init {
    viewModelScope.launch { getAllPokemon() }
  }

  private suspend fun getAllPokemon() {
    try {
      val response: Response<PokemonResults> = webservice.getPokemons()
      val results: PokemonResults? = response.body()
      if (response.isSuccessful && results != null) {
        stateFlow.value = HomeViewState.Success(results)
      } else {
        stateFlow.value = HomeViewState.Error
      }
    } catch (t: Throwable) {
      stateFlow.value = HomeViewState.Error
    }
  }
}
