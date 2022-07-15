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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Loading
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Failed
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Refreshing
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
  override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?,
  ): View = inflater.inflate(R.layout.home_view_controller, container, false)

  override fun onAttach(view: View) {
    val refreshView: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
    val recyclerView: RecyclerView = view.findViewById(R.id.home_recyclerView)
    val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    val errorImageView: ImageView = view.findViewById(R.id.image_error)
    val layoutManager = LinearLayoutManager(applicationContext)
    val adapter = HomeListAdapter(router)

    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter

    refreshView.setOnRefreshListener {
      launch {
        viewModel.getAllPokemon(true)
      }
    }

    launch {
      viewModel.states
        .collect { state: HomeViewState ->
          if (state is Refreshing || state is Loading) {
            refreshView.isRefreshing = true
            progressBar.isVisible = true
          } else {
            refreshView.isRefreshing = false
            progressBar.isVisible = false
          }
          recyclerView.isVisible = state is Success
          errorImageView.isVisible = state is Failed
          if (state !is Success) return@collect
          adapter.setPokemonResults(state.results)
        }
    }
  }

  override fun onDetach(view: View) {
    coroutineContext.cancelChildren()
  }
}
