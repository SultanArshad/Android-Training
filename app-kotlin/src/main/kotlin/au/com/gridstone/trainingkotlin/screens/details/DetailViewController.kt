package au.com.gridstone.trainingkotlin.screens.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import au.com.gridstone.trainingkotlin.POKEMON
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.SCOPE_DETAIL
import au.com.gridstone.trainingkotlin.data.Pokemon
import au.com.gridstone.trainingkotlin.screens.details.DetailViewEvent.BackClick
import au.com.gridstone.trainingkotlin.screens.details.DetailViewEvent.Refresh
import com.bluelinelabs.conductor.Controller
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class DetailViewController(bundle: Bundle) : Controller(bundle), KoinComponent,
  CoroutineScope {
  private val pokemon: Pokemon = bundle.getParcelable(POKEMON)!!

  internal constructor(pokemon: Pokemon) : this(bundleOf(POKEMON to pokemon))

  private val scope: Scope = getKoin().getOrCreateScope(pokemon.id, named(SCOPE_DETAIL))
  private val viewModel: DetailViewModel = scope.get { parametersOf(pokemon.id) }
  override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?,
  ): View = inflater.inflate(R.layout.detail_view_controller, container, false)

  override fun onAttach(view: View) {
    val presenter = DetailViewPresenter(view, pokemon)

    launch {
      presenter.events.collect { event: DetailViewEvent ->
        when (event) {
          is Refresh -> launch((Dispatchers.IO)) { viewModel.getPokemonDetail(pokemon.id) }
          is BackClick -> router.popCurrentController()
        }
      }
    }

    launch {
      viewModel.states
        .collect { state: DetailViewState ->
          presenter.display(state)
        }
    }
  }

  override fun onDetach(view: View) {
    coroutineContext.cancelChildren()
  }

  override fun onDestroy() {
    scope.close()
  }
}
