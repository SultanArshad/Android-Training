@file:OptIn(ExperimentalCoroutinesApi::class)

package au.com.gridstone.trainingkotlin

import au.com.gridstone.trainingkotlin.api.PokemonService
import au.com.gridstone.trainingkotlin.data.PokemonResults
import au.com.gridstone.trainingkotlin.screens.home.HomeViewModel
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Failed
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Loading
import au.com.gridstone.trainingkotlin.screens.home.HomeViewState.Success
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import retrofit2.Response

class HomeDomainTests : KoinTest {
  private val viewModel: HomeViewModel by inject()
  private val service: PokemonService by inject()

  private val testModule: Module = module {
    single<PokemonService> { mockk() }
    single { HomeViewModel() }
  }

  @Before
  fun setUp() {
    Dispatchers.setMain(StandardTestDispatcher())
    startKoin {
      printLogger()
      modules(testModule)
    }
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    StandardTestDispatcher().cancelChildren()
    stopKoin()
  }

  @Test
  fun `Test successful pokemon`() = runTest {
    coEvery { service.getPokemons() } returns
        Response.success(PokemonResults(testPokemon))

    val (
      loading: HomeViewState,
      success: HomeViewState,
    ) = viewModel.states.take(2).toList()

    assertEquals(Loading, loading)
    assertEquals(Success(PokemonResults(testPokemon)), success)
  }

  @Test
  fun `Test unsuccessful pokemon`() = runTest {
    coEvery { service.getPokemons() } returns
        Response.error(500, ResponseBody.create(null, "Error"))

    val (
      loading: HomeViewState,
      fail: HomeViewState,
    ) = viewModel.states.take(2).toList()

    assertEquals(Loading, loading)
    assertEquals(Failed, fail)
  }
}
