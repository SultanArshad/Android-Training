@file:OptIn(ExperimentalCoroutinesApi::class)

package au.com.gridstone.trainingkotlin

import au.com.gridstone.trainingkotlin.api.PokemonService
import au.com.gridstone.trainingkotlin.data.PokemonData
import au.com.gridstone.trainingkotlin.screens.details.DetailViewModel
import au.com.gridstone.trainingkotlin.screens.details.DetailViewState
import au.com.gridstone.trainingkotlin.screens.details.DetailViewState.Failed
import au.com.gridstone.trainingkotlin.screens.details.DetailViewState.Loading
import au.com.gridstone.trainingkotlin.screens.details.DetailViewState.Success
import io.mockk.coEvery
import io.mockk.mockk
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
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import retrofit2.Response

private const val testPokemonId: String = "000000"

class DetailDomainTest : KoinTest {
  private val viewModel: DetailViewModel by inject()
  private val service: PokemonService by inject()


  private val testModule: Module = module {
    single<PokemonService> { mockk() }
    single { DetailViewModel(testPokemonId) }
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
  fun `Test successful pokemon details`() = runTest {
    coEvery { service.getPokemonDetails(testPokemonId) } returns
        Response.success(PokemonData(testPokemonDataStats))

    val (loading: DetailViewState, success: DetailViewState) = viewModel.states.take(2)
      .toList()
    assertEquals(Loading, loading)
    assertEquals(Success(PokemonData(testPokemonDataStats)), success)
  }

  @Test
  fun `Test unsuccessful pokemon details`() = runTest {
    coEvery { service.getPokemonDetails(testPokemonId) } returns
        Response.error(500, ResponseBody.create(null, "Error"))

    val (loading: DetailViewState, fail: DetailViewState) = viewModel.states.take(2).toList()
    assertEquals(Loading, loading)
    assertEquals(Failed, fail)
  }

}
