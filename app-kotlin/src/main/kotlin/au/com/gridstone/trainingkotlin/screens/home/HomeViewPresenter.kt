package au.com.gridstone.trainingkotlin.screens.home

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Failed
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Loading
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes

class HomeViewPresenter(view: View) {
  private val refreshView: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
  private val recyclerView: RecyclerView = view.findViewById(R.id.home_recyclerView)
  private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
  private val errorImage: ImageView = view.findViewById(R.id.image_error)
  private val adapter = HomeListAdapter()

  val events: Flow<HomeViewEvents> = merge(
    refreshView.refreshes().map { HomeViewEvents.Refresh },
    adapter.events,
  )

  init {
    val layoutManager = LinearLayoutManager(view.context)
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter
  }

  fun display(state: HomeViewState) {
    refreshView.isRefreshing = state is Loading
    progressBar.isVisible = state is Loading
    recyclerView.isVisible = state is Success
    errorImage.isVisible = state is Failed
    if (state !is Success) return
    refreshView.isRefreshing = state !is Failed
    refreshView.isRefreshing = false
    adapter.setPokemonResults(state.results)
  }

}
