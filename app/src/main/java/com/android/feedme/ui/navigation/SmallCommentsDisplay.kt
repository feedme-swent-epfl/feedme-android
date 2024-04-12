package com.android.feedme.ui.navigation

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.feedme.R
import com.android.feedme.model.data.Comment
import com.android.feedme.ui.theme.BlueUser
import com.android.feedme.ui.theme.YellowStar
import java.util.Date

@Composable
fun SmallCommentsDisplay(listComment: List<Comment>) {
  LazyColumn {
      items(listComment) {item ->
          //ItemRow(item = item)
          CommentCard(comment = item)
      }
  }
}


@Composable
fun CommentCard(comment: Comment) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Recipe image
            Image(
                painter = painterResource(id = R.drawable.test_image_pasta),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Title and description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = comment.authorId,
                    style = MaterialTheme.typography.bodySmall,
                    color = BlueUser
                )
                //Spacer(modifier = Modifier.height(4.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = comment.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
@Preview
@Composable
fun Preview(){
    val comment1 = Comment("@author","@author","https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images",
        3.5,1.15,"Was uncooked","I respected instruction, but the result wasn't great", Date()
    )
    val listComment = listOf(comment1, comment1, comment1)
    SmallCommentsDisplay(listComment = listComment)
}