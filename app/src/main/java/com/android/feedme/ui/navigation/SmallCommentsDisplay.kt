package com.android.feedme.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Comment
import com.android.feedme.ui.theme.YellowStar
import java.util.Date

@Composable
fun SmallCommentsDisplay(listComment: List<Comment>) {
  val IMAGE_WIDTH = LocalConfiguration.current.screenWidthDp / 2

  LazyVerticalGrid(columns = GridCells.Adaptive(minSize = IMAGE_WIDTH.dp)) {
    items(listComment.size) { i ->
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
              .testTag("Column")
              .padding(3.dp)) {

            // Recipe photo, downloaded from internet
            AsyncImage(
                model = listComment[i].photoURL,
                contentDescription = "Comment Image",
                modifier = Modifier.testTag("Recipe Image"))

            Row(verticalAlignment = Alignment.CenterVertically) {

              // Star icon for ratings
              Icon(
                  imageVector = Icons.TwoTone.Star,
                  contentDescription = null,
                  tint = YellowStar,
                  modifier = Modifier
                      .testTag("Star Icon")
                      .padding(end = 3.dp))

              // Recipe rating
              Text(
                  listComment[i].rating.toString(),
                  modifier = Modifier
                      .testTag("Rating")
                      .padding(end = 10.dp))

              // Clock icon for the time
              // There is no clock icon in Material, so for now i'm using the "build" icon
              Icon(
                  imageVector = Icons.Outlined.Info,
                  contentDescription = null,
                  modifier = Modifier
                      .testTag("Info Icon")
                      .padding(end = 3.dp))

              // Recipe time
              Text(
                  listComment[i].time.toString(),
                  modifier = Modifier
                      .testTag("Time")
                      .padding(end = 45.dp))
            }
          Text(listComment[i].title)
          }
    }
  }
}

@Preview
@Composable
fun Preview(){
    val comment1 = Comment("author","author","https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images",
        3.5,1.15,"Was unccoked","I respected instruction, but the result wasn't great", Date()
    )
    val listComment = listOf(comment1, comment1, comment1)
    SmallCommentsDisplay(listComment = listComment)
}