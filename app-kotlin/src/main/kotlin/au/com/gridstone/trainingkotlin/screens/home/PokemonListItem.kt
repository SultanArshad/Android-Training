package au.com.gridstone.trainingkotlin.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import au.com.gridstone.trainingkotlin.BuildConfig
import au.com.gridstone.trainingkotlin.R
import au.com.gridstone.trainingkotlin.data.Pokemon
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun PokemonListItem(pokemon: Pokemon, onSelectPokemon: (Pokemon) -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onSelectPokemon(pokemon) }
      .padding(horizontal = 8.dp, vertical = 8.dp),
    elevation = 2.dp,
    shape = RoundedCornerShape(corner = CornerSize(16.dp))
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .padding(start = 8.dp, end = 8.dp)
        .fillMaxWidth()
    ) {
      Text(
        modifier = Modifier
          .padding(start = 8.dp)
          .weight(1f),
        text = pokemon.name.uppercase(),
        style = typography.h6,
        textAlign = TextAlign.Left,
      )
      GlideImage(
        modifier = Modifier
          .size(150.dp)
          .weight(1f),
        imageModel = "${BuildConfig.IMAGE_URL}/${pokemon.id}.png",
        // Crop, Fit, Inside, FillHeight, FillWidth, None
        contentScale = ContentScale.Inside,
        // shows an image with a circular revealed animation.
        circularReveal = CircularReveal(duration = 300),
        // shows a placeholder ImageBitmap when loading.
        placeHolder = ImageVector.vectorResource(id = R.drawable.ic_placeholder),
        // shows an error ImageBitmap when the request failed.
        error = ImageVector.vectorResource(id = R.drawable.ic_error_round)
      )
    }
  }
}
