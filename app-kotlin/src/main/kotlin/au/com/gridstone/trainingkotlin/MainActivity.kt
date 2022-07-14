package au.com.gridstone.trainingkotlin

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import au.com.gridstone.trainingkotlin.views.HomeViewController
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction

class MainActivity : AppCompatActivity() {

  private var router: Router? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val viewGroup = findViewById<ViewGroup>(R.id.home_container)
    router = Conductor.attachRouter(this, viewGroup, savedInstanceState)
    router?.setRoot(RouterTransaction.with(HomeViewController()))
  }

  override fun onBackPressed() {
    if (router?.handleBack() != true) {
      super.onBackPressed()
    }
  }
}
