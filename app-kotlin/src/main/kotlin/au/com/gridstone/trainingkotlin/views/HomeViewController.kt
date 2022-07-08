package au.com.gridstone.trainingkotlin.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.adapters.HomeListAdapter
import au.com.gridstone.trainingkotlin.viewmodels.HomeViewModel
import au.com.gridstone.trainingkotlin.viewmodels.HomeViewState
import au.com.gridstone.trainingkotlin.viewmodels.HomeViewState.*
import com.bluelinelabs.conductor.Controller
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
  override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?,
  ): View = inflater.inflate(R.layout.home_view_controller, container, false)

  override fun onAttach(view: View) {
    val recyclerView: RecyclerView = view.findViewById(R.id.home_recyclerView)
    val progressBar: ProgressBar = view.findViewById(R.id.home_progressBar)
    val errorImage: ImageView = view.findViewById(R.id.image_error)
    val layoutManager = LinearLayoutManager(applicationContext)
    recyclerView.layoutManager = layoutManager
    val homeListAdapter = HomeListAdapter(router)
    recyclerView.adapter = homeListAdapter
    launch(Dispatchers.Main) {
      viewModel.state
        .collect { state: HomeViewState ->
          progressBar.isVisible = state is Loading
          recyclerView.isVisible = state is Success
          errorImage.isVisible = state is Error
          if (state !is Success) return@collect
          homeListAdapter.setPokemonResults(state.results)
        }
    }
  }

  override fun onDetach(view: View) {
    coroutineContext.cancelChildren()
  }
}
