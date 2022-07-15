package au.com.gridstone.trainingkotlin.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.gridstone.trainingkotlin.api.PokemonService
import au.com.gridstone.trainingkotlin.data.PokemonData
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import retrofit2.Response

sealed class DetailViewState {
  object Loading : DetailViewState()
  object Failed : DetailViewState()
  data class Success(val result: PokemonData) : DetailViewState()
}

sealed class DetailViewEvent {
  object BackClick : DetailViewEvent()
  object Refresh : DetailViewEvent()
}

class DetailViewModel(id: String) : ViewModel(), KoinComponent {

  private val stateFlow: MutableStateFlow<DetailViewState> =
    MutableStateFlow(DetailViewState.Loading)

  val states: StateFlow<DetailViewState> = stateFlow
  private val webservice: PokemonService = get()

  init {
    viewModelScope.launch { getPokemonDetail(id) }
  }

  suspend fun getPokemonDetail(id: String) {
    stateFlow.value = DetailViewState.Loading
    try {
      val response: Response<PokemonData> = webservice.getPokemonDetails(id)
      val result: PokemonData? = response.body()
      if (response.isSuccessful && result != null) {
        stateFlow.value = DetailViewState.Success(result)
      } else {
        stateFlow.value = DetailViewState.Failed
      }
    } catch (e: IllegalArgumentException) {
      stateFlow.value = DetailViewState.Failed
    } catch (e: UnknownHostException) {
      stateFlow.value = DetailViewState.Failed
    } catch (e: SocketTimeoutException) {
      stateFlow.value = DetailViewState.Failed
    }
  }
}
