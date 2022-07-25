package au.com.gridstone.trainingkotlin.screens.home

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Failed
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Loading
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Success
import kotlinx.coroutines.flow.Flow

class HomeViewPresenter(view: View) {
  private val recyclerView: RecyclerView = view.findViewById(R.id.home_recyclerView)
  private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
  private val errorImage: ImageView = view.findViewById(R.id.image_error)
  private val adapter = HomeListAdapter()

  val events: Flow<HomeViewEvents> = adapter.events

  init {
    val layoutManager = LinearLayoutManager(view.context)
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter
  }

  fun display(state: HomeViewState) {
    progressBar.isVisible = state is Loading
    recyclerView.isVisible = state is Success
    errorImage.isVisible = state is Failed
    if (state !is Success) return
    adapter.setPokemonResults(state.results)
  }
}
