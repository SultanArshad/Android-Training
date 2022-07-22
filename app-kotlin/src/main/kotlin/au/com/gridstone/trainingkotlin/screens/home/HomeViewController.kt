package au.com.gridstone.trainingkotlin.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.screens.details.DetailViewController
import au.com.gridstone.trainingkotlin.screens.home.HomeViewEvents.ItemClick
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.coroutines.CoroutineContext

class HomeViewController : Controller(), KoinComponent, CoroutineScope {

  private val viewModel: HomeViewModel = get()
  override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?,
  ): View = inflater.inflate(R.layout.home_view_controller, container, false)

  override fun onAttach(view: View) {

    val presenter = HomeViewPresenter(view)

    launch {
      presenter.events.collect { event: HomeViewEvents ->
        when (event) {
          is ItemClick -> router.pushController(
            RouterTransaction.with(DetailViewController(event.pokemon))
              .pushChangeHandler(HorizontalChangeHandler())
              .popChangeHandler(HorizontalChangeHandler())
          )
        }
      }
    }

    launch {
      viewModel.states
        .collect { state: HomeViewState ->
          presenter.display(state)
        }
    }
  }

  override fun onDetach(view: View) {
    coroutineContext.cancelChildren()
  }
}
