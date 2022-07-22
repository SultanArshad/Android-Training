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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

internal class HomeListAdapter() : RecyclerView.Adapter<HomeListAdapter.MyViewHolder>() {

  private var itemsList: List<Pokemon> = emptyList()
  private val eventsChannel: Channel<HomeViewEvents.ItemClick> = Channel()
  val events: Flow<HomeViewEvents> = eventsChannel.receiveAsFlow()

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
      val event = HomeViewEvents.ItemClick(item)
      eventsChannel.trySend(event)
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
