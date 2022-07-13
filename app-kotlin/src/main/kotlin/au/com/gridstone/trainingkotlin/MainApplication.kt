package au.com.gridstone.trainingkotlin

import android.app.Application
import au.com.gridstone.trainingkotlin.BuildConfig.BASAE_URL
import au.com.gridstone.trainingkotlin.apiservices.PokemonService
import au.com.gridstone.trainingkotlin.viewmodels.HomeViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val POKEMON: String = "pokemon"
const val SCOPE_DETAIL: String = "detailScope"

val applicationModule: Module = module {
  single<Retrofit> {
    Retrofit.Builder()
      .baseUrl(BASAE_URL)
      .client(get())
      .addConverterFactory(MoshiConverterFactory.create(get()))
      .build()
  }
  single {
    Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }
  single {
    OkHttpClient()
      .newBuilder()
      .addInterceptor(LoggingInterceptor)
      .build()
  }

  single<PokemonService> {
    get<Retrofit>().create(PokemonService::class.java)
  }

  single { HomeViewModel() }
}

class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidContext(this@MainApplication)
      modules(applicationModule)
    }
  }
}
