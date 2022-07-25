package au.com.gridstone.trainingkotlin.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.gridstone.trainingkotlin.api.PokemonService
import au.com.gridstone.trainingkotlin.data.Pokemon
import au.com.gridstone.trainingkotlin.data.PokemonResults
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import retrofit2.Response

sealed class HomeViewState {
  object Loading : HomeViewState()
  object Failed : HomeViewState()
  data class Success(val results: PokemonResults) : HomeViewState()
}

sealed class HomeViewEvents {
  data class ItemClick(val pokemon: Pokemon) : HomeViewEvents()
}

class HomeViewModel() : ViewModel(), KoinComponent {
  private val stateFlow: MutableStateFlow<HomeViewState> = MutableStateFlow(HomeViewState.Loading)
  val states: StateFlow<HomeViewState> = stateFlow
  private val webservice: PokemonService = get()

  init {
    viewModelScope.launch { getAllPokemon() }
  }

  suspend fun getAllPokemon() {
    stateFlow.value = HomeViewState.Loading
    try {
      val response: Response<PokemonResults> = webservice.getPokemons()
      val results: PokemonResults? = response.body()
      if (response.isSuccessful && results != null) {
        stateFlow.value = HomeViewState.Success(results)
      } else {
        stateFlow.value = HomeViewState.Failed
      }
    } catch (e: IllegalArgumentException) {
      stateFlow.value = HomeViewState.Failed
    } catch (e: UnknownHostException) {
      stateFlow.value = HomeViewState.Failed
    } catch (e: SocketTimeoutException) {
      stateFlow.value = HomeViewState.Failed
    }
  }
}
