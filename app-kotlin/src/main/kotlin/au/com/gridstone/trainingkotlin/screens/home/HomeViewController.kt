package au.com.gridstone.trainingkotlin.screens.home

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
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Loading
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Failed
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Success
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
    val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    val errorImage: ImageView = view.findViewById(R.id.image_error)
    val layoutManager = LinearLayoutManager(applicationContext)
    recyclerView.layoutManager = layoutManager
    val homeListAdapter = HomeListAdapter(router)
    recyclerView.adapter = homeListAdapter

    launch(Dispatchers.Main) {
      viewModel.states
        .collect { state: HomeViewState ->
          progressBar.isVisible = state is Loading
          recyclerView.isVisible = state is Success
          errorImage.isVisible = state is Failed
          if (state !is Success) return@collect
          homeListAdapter.setPokemonResults(state.results)
        }
    }
  }

  override fun onDetach(view: View) {
    coroutineContext.cancelChildren()
  }
}
