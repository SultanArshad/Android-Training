package au.com.gridstone.trainingkotlin.screens.details

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import au.com.gridstone.trainingkotlin.BuildConfig
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.data.Pokemon
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes
import ru.ldralighieri.corbind.view.clicks

class DetailViewPresenter(view: View, private val pokemon: Pokemon) {
  private val toolbar: Toolbar = view.findViewById(R.id.detail_toolbar)
  private val detailView: LinearLayout = view.findViewById(R.id.detail_view)
  private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
  private val errorImageView: ImageView = view.findViewById(R.id.image_error)
  private val attackView: TextView = view.findViewById(R.id.attack_value)
  private val spAttackView: TextView = view.findViewById(R.id.sp_attack_value)
  private val defenseView: TextView = view.findViewById(R.id.defense_value)
  private val spDefenseView: TextView = view.findViewById(R.id.sp_defense_value)
  private val speedView: TextView = view.findViewById(R.id.speed_value)
  private val hpView: TextView = view.findViewById(R.id.hp_value)
  private val imageView: ImageView = view.findViewById(R.id.image)
  private val refreshView: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)

  val events: Flow<DetailViewEvent> = merge(
    refreshView.refreshes().map { DetailViewEvent.Refresh },
    toolbar.clicks().map { DetailViewEvent.BackClick }
  )

  init {
    toolbar.title = pokemon.name
  }

  fun display(state: DetailViewState) {
    refreshView.isRefreshing = state is DetailViewState.Loading
    progressBar.isVisible = state is DetailViewState.Loading
    detailView.isVisible = state is DetailViewState.Success
    errorImageView.isVisible = state is DetailViewState.Failed

    if (state !is DetailViewState.Success) return
    refreshView.isRefreshing = false
    hpView.text = state.result.stats[0].base_stat.toString()
    attackView.text = state.result.stats[1].base_stat.toString()
    defenseView.text = state.result.stats[2].base_stat.toString()
    spAttackView.text = state.result.stats[3].base_stat.toString()
    spDefenseView.text = state.result.stats[4].base_stat.toString()
    speedView.text = state.result.stats[5].base_stat.toString()

    Picasso.get()
      .load("${BuildConfig.IMAGE_URL}/${pokemon.id}.png")
      .placeholder(R.drawable.ic_placeholder)
      .fit()
      .into(imageView)
  }
}
