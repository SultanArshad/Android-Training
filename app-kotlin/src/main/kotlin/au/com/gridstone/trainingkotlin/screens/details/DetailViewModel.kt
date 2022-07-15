package au.com.gridstone.trainingkotlin.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.gridstone.trainingkotlin.api.PokemonService
import au.com.gridstone.trainingkotlin.data.PokemonData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import retrofit2.Response

sealed class DetailViewState {
  object Loading : DetailViewState()
  object Error : DetailViewState()
  data class Success(val result: PokemonData) : DetailViewState()
}

class DetailViewModel(id: String) : ViewModel(), KoinComponent {

  private val stateFlow: MutableStateFlow<DetailViewState> =
    MutableStateFlow(DetailViewState.Loading)
  val state: StateFlow<DetailViewState> = stateFlow
  private val webservice: PokemonService = get()

  init {
    viewModelScope.launch { getPokemonDetail(id) }
  }

  private suspend fun getPokemonDetail(id: String) {
    try {
      val response: Response<PokemonData> = webservice.getPokemonDetails(id)
      val result: PokemonData? = response.body()
      if (response.isSuccessful && result != null) {
        stateFlow.value = DetailViewState.Success(result)
      } else {
        stateFlow.value = DetailViewState.Error
      }
    } catch (t: Throwable) {
      stateFlow.value = DetailViewState.Error
    }
  }
}
