package au.com.gridstone.trainingkotlin.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import au.com.gridstone.trainingkotlin.BuildConfig.IMAGE_URL
import au.com.gridstone.trainingkotlin.POKEMON
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.SCOPE_DETAIL
import au.com.gridstone.trainingkotlin.data.Pokemon
import au.com.gridstone.trainingkotlin.viewmodels.DetailViewModel
import au.com.gridstone.trainingkotlin.viewmodels.DetailViewState
import com.bluelinelabs.conductor.Controller
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import org.koin.core.component.*
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import kotlin.coroutines.CoroutineContext

class DetailViewController(bundle: Bundle) : Controller(bundle), KoinComponent,
  CoroutineScope {
  private val pokemon: Pokemon = bundle.getParcelable(POKEMON)!!

  internal constructor(pokemon: Pokemon) : this(bundleOf(POKEMON to pokemon))

  private val scope: Scope = getKoin().getOrCreateScope(pokemon.id, named(SCOPE_DETAIL))
  private val viewModel: DetailViewModel = scope.get { parametersOf(pokemon.id) }
  override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?,
  ): View = inflater.inflate(R.layout.detail_view_controller, container, false)

  override fun onAttach(view: View) {
    val toolbar: Toolbar = view.findViewById(R.id.detail_toolbar)
    val detailView: LinearLayout = view.findViewById(R.id.detail_view)
    val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    val errorImageView: ImageView = view.findViewById(R.id.image_error)
    val nameView: TextView = view.findViewById(R.id.name)
    val attackView: TextView = view.findViewById(R.id.attack_value)
    val spAttackView: TextView = view.findViewById(R.id.sp_attack_value)
    val defenseView: TextView = view.findViewById(R.id.defense_value)
    val spDefenseView: TextView = view.findViewById(R.id.sp_defense_value)
    val speedView: TextView = view.findViewById(R.id.speed_value)
    val hpView: TextView = view.findViewById(R.id.hp_value)
    val imageView: ImageView = view.findViewById(R.id.image)

    toolbar.title = pokemon.name
    toolbar.setNavigationOnClickListener {
      router.popCurrentController()
    }

    launch(Dispatchers.Main) {
      viewModel.state
        .collect { state: DetailViewState ->
          progressBar.isVisible = state is DetailViewState.Loading
          detailView.isVisible = state is DetailViewState.Success
          errorImageView.isVisible = state is DetailViewState.Error
          if (state !is DetailViewState.Success) return@collect

          //populating data on views
          nameView.text = pokemon.name
          hpView.text = state.result.stats[0].base_stat.toString()
          attackView.text = state.result.stats[1].base_stat.toString()
          defenseView.text = state.result.stats[2].base_stat.toString()
          spAttackView.text = state.result.stats[3].base_stat.toString()
          spDefenseView.text = state.result.stats[4].base_stat.toString()
          speedView.text = state.result.stats[5].base_stat.toString()

          Picasso.get()
            .load("${IMAGE_URL}/${pokemon.id}.png")
            .placeholder(R.drawable.ic_placeholder)
            .fit()
            .into(imageView)
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
