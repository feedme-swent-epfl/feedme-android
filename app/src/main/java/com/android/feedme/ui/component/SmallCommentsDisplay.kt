package com.android.feedme.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.feedme.R
import com.android.feedme.model.data.Comment
import com.android.feedme.ui.theme.BlueUsername

/**
 * Composable function to display a list of small comments.
 *
 * This function creates a LazyColumn to display a list of comments on below the other. Each comment
 * is displayed using the [CommentCard] composable.
 *
 * @param listComment The list of [Comment] to be displayed.
 */
@Composable
fun SmallCommentsDisplay(listComment: List<Comment>, modifier: Modifier = Modifier) {
    // Calculate the width of each image based on the screen width, we want to display 2 images per
    // line
    val imageWidth = LocalConfiguration.current.screenWidthDp / 2

    // Calculate the height of the grid based on the number of recipes and the height of each card
    // 216 is the height of each card with padding
    val gridHeight = ((listComment.size / 2) + (listComment.size % 2)) * 216

    // Using this instead of a LazyColumn should fix the "infinite scroll" bug
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = imageWidth.dp),
        userScrollEnabled = false,
        modifier = modifier.height(gridHeight.dp)
    ) {
        items(listComment) { item -> CommentCard(comment = item) } }
}

/**
 * Composable function to display a single comment.
 *
 * This function creates a Surface with a Row layout to display a comment. It contains an image
 * representing the commenter, along with the author's name, the comment's title, and the comment's
 * content.
 *
 * @param comment The [Comment] object to be displayed.
 */
@Composable
fun CommentCard(comment: Comment) {
  Surface(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
      color = Color.White,
      shape = RoundedCornerShape(8.dp),
      border = BorderStroke(2.dp, Color.Black)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

          // TODO replace this by the photo uploading code
          Image(
              painter = painterResource(id = R.drawable.test_image_pasta),
              contentDescription = "Recipe Image",
              modifier = Modifier.size(100.dp).aspectRatio(1f).clip(RoundedCornerShape(8.dp)),
              contentScale = ContentScale.Crop)

          Spacer(modifier = Modifier.width(10.dp))

          // Author + title + description
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(start = 16.dp)
                      .align(Alignment.CenterVertically)) {

                // Comment authorId
                Text(
                    text = comment.commentId,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BlueUsername,
                    fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                // Comment title
                Text(
                    text = comment.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(8.dp))
                // Comment description
                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis)
              }
        }
      }
}
