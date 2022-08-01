package au.com.gridstone.trainingkotlin.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.data.Pokemon
import au.com.gridstone.trainingkotlin.screens.details.DetailViewController
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Failed
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Loading
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Success
import au.com.gridstone.trainingkotlin.ui.theme.TrainingKotlinTheme
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HomeViewController : Controller(), KoinComponent {

  private val viewModel: HomeViewModel = get()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?,
  ): View {
    return ComposeView(container.context).apply {
      setContent {
        TrainingKotlinTheme() {
          PokemonHomeContent(
            viewModel = viewModel,
            onSelectPokemon = {
              router.pushController(
                RouterTransaction.with(DetailViewController(pokemon = it))
                  .popChangeHandler(HorizontalChangeHandler())
                  .pushChangeHandler(HorizontalChangeHandler())
              )
            }
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PokemonHomeContent(viewModel: HomeViewModel, onSelectPokemon: (Pokemon) -> Unit) {

  val state: HomeViewState by viewModel.states.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar {
        Text(
          text = stringResource(id = R.string.pokemon),
          style = MaterialTheme.typography.h6,
        )
      }
    }
  ) { padding ->
    SwipeRefresh(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      state = rememberSwipeRefreshState(isRefreshing = state == Loading),
      onRefresh = { with(viewModel) { launch { getAllPokemon() } } }
    ) {
      AnimatedContent(targetState = state) {
        when (val _state: HomeViewState = state) {
          Loading -> LoadingView()
          Failed -> FailedView()
          is Success -> PokemonList(
            _state.results.results,
            onSelectPokemon = onSelectPokemon
          )
        }
      }
    }
  }
}

@Composable
fun FailedView() {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_error_round),
      contentDescription = "Error",
      modifier = Modifier
        .size(48.dp),
      alignment = Alignment.Center,
    )
    Text(text = "Something went wrong")
  }
}

@Composable
fun LoadingView() {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize()
  ) {
    CircularProgressIndicator(
      modifier = Modifier.size(32.dp),
      strokeWidth = 2.dp
    )
  }
}

@Composable
fun PokemonList(results: List<Pokemon>, onSelectPokemon: (Pokemon) -> Unit) {
  LazyColumn(
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
  ) {
    items(results) {
      PokemonListItem(pokemon = it, onSelectPokemon = onSelectPokemon)
    }
  }
}
