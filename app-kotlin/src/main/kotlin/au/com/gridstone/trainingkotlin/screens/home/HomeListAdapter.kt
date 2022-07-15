package au.com.gridstone.trainingkotlin.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import au.com.gridstone.trainingkotlin.BuildConfig.IMAGE_URL
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.data.PokemonResults
import au.com.gridstone.trainingkotlin.data.Pokemon
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import au.com.gridstone.trainingkotlin.screens.details.DetailViewController
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.squareup.picasso.Picasso

internal class HomeListAdapter(private val router: Router) :
  RecyclerView.Adapter<HomeListAdapter.MyViewHolder>() {
  private var itemsList: List<Pokemon> = emptyList()

  internal class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: ImageView = view.findViewById(R.id.image_list)
    val name: TextView = view.findViewById(R.id.name)
  }

  @NonNull
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView: View = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_home_recyclerview, parent, false)
    return MyViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val item: Pokemon = itemsList[position]
    holder.name.text = item.name
    loadImage(holder, item)
    holder.itemView.setOnClickListener {
      router.pushController(RouterTransaction.with(
        DetailViewController(item))
        .pushChangeHandler(HorizontalChangeHandler())
        .popChangeHandler(HorizontalChangeHandler())
      )
    }
  }

  private fun loadImage(holder: MyViewHolder, item: Pokemon) =
    Picasso.get()
      .load("$IMAGE_URL/${item.id}.png")
      .placeholder(R.drawable.ic_placeholder)
      .into(holder.image)

  override fun getItemCount(): Int = itemsList.size

  fun setPokemonResults(it: PokemonResults) {
    itemsList = it.results
    notifyDataSetChanged()
  }
}
