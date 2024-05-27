package com.android.feedme.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Comment
import com.android.feedme.model.viewmodel.CommentViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.YellowStar
import com.android.feedme.ui.theme.deleteButtonColor

/**
 * Displays a window for creating a comment, allowing the user to input a title, description, and
 * rating. The comment can then be published or canceled.
 *
 * @param profileViewModel The ViewModel associated with the user's profile.
 * @param recipeViewModel The ViewModel associated with the recipe for which the comment is being
 *   made.
 * @param commentViewModel The ViewModel managing the comments.
 * @param onDismiss Callback function to be called when the user cancels the comment creation.
 */
@Composable
fun CreateComment(
    profileViewModel: ProfileViewModel,
    recipeViewModel: RecipeViewModel,
    commentViewModel: CommentViewModel,
    onDismiss: () -> Unit
) {
  var commentTitle by remember { mutableStateOf("") }
  var rating by remember { mutableStateOf(0.0) }
  var description by remember { mutableStateOf("") }

  Box(
      Modifier.fillMaxSize().padding(16.dp).background(Color.Transparent).testTag("OuterBox"),
      contentAlignment = Alignment.Center) {
        Column(
            modifier =
                Modifier.width(350.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .testTag("InnerCol"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row(
                  modifier = Modifier.fillMaxWidth().testTag("FirstRow"),
                  horizontalArrangement = Arrangement.SpaceBetween) {
                    // TODO Change that and call the function UserProfilePicture(profileViewModel)
                    AsyncImage(
                        modifier =
                            Modifier.width(100.dp)
                                .height(100.dp)
                                .clip(CircleShape)
                                .border(1.dp, color = Color.LightGray, shape = CircleShape)
                                .testTag("ProfileIcon"),
                        model =
                            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww." +
                                "generation-souvenirs.com%2F38509-thickbox_default%2Fpeluche-" +
                                "bisounours-rose-toucalin-30-cm.jpg&f=1&nofb=1&ipt=411c19cdad14" +
                                "03db0340c05652681d988095a71011a275f235f20faced305a21&ipo=images",
                        contentDescription = "User Profile Image",
                        contentScale = ContentScale.FillBounds)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(start = 5.dp, top = 35.dp)
                                .testTag("StarRow")) {
                          repeat(5) { index ->
                            val isSelected = index + 1 <= rating
                            val isHalfSelected = index + 0.5 == rating
                            val iconTint =
                                when {
                                  isSelected || isHalfSelected -> YellowStar
                                  else -> Color.Black
                                }

                            Icon(
                                tint = iconTint,
                                imageVector =
                                    when {
                                      isSelected -> Icons.Rounded.Star
                                      isHalfSelected -> Icons.AutoMirrored.Rounded.StarHalf
                                      else -> Icons.Rounded.StarBorder
                                    },
                                contentDescription = "",
                                modifier =
                                    Modifier.clickable {
                                          rating =
                                              when {
                                                isSelected ->
                                                    if (isHalfSelected) index.toDouble()
                                                    else index.toFloat() + 0.5
                                                else -> index.toDouble() + 1
                                              }
                                        }
                                        .size(30.dp)
                                        .testTag("star$index"))
                          }
                        }
                  }
              // Title
              OutlinedTextField(
                  value = commentTitle,
                  onValueChange = { commentTitle = it },
                  placeholder = {
                    Text(text = "Title", color = Color.LightGray, fontStyle = FontStyle.Italic)
                  },
                  modifier = Modifier.fillMaxWidth().testTag("TitleField"),
                  shape = RoundedCornerShape(10.dp))

              // Description
              OutlinedTextField(
                  value = description,
                  onValueChange = { description = it },
                  placeholder = {
                    Text(
                        text = "Description", color = Color.LightGray, fontStyle = FontStyle.Italic)
                  },
                  modifier = Modifier.fillMaxWidth().height(200.dp).testTag("DescriptionField"),
                  shape = RoundedCornerShape(10.dp))

              // Delete and Publish buttons
              Row(
                  modifier = Modifier.fillMaxWidth().testTag("ButtonRow"),
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  verticalAlignment = Alignment.CenterVertically) {
                    // Delete button
                    OutlinedButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        border = BorderStroke(2.dp, deleteButtonColor),
                        modifier = Modifier.weight(1f).height(35.dp).testTag("DeleteButton"),
                        shape = RoundedCornerShape(20.dp)) {
                          Text(
                              "Cancel",
                              color = deleteButtonColor,
                              fontWeight = FontWeight.Medium,
                              fontSize = 16.sp)
                        }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Publish button
                    OutlinedButton(
                        onClick = {
                          val com =
                              Comment(
                                  "ID_DEFAULT",
                                  profileViewModel.currentUserId ?: "ID_DEFAULT",
                                  recipeViewModel.recipe.value?.recipeId ?: "ID_DEFAULT",
                                  "URL_DEFAULT",
                                  rating,
                                  commentTitle,
                                  description,
                                  java.util.Date())
                          if (commentTitle.isNotEmpty() && description.isNotEmpty()) {
                            commentViewModel.addComment(com) {
                              // TODO Add the comment Id to profile and recipe locally and in the db
                            }
                          }
                          onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        border = BorderStroke(2.dp, TemplateColor),
                        modifier = Modifier.weight(1f).height(35.dp).testTag("PublishButton"),
                        shape = RoundedCornerShape(20.dp)) {
                          Text(
                              "Publish",
                              color = TemplateColor,
                              fontWeight = FontWeight.Medium,
                              fontSize = 16.sp,
                              maxLines = 1)
                        }
                  }
            }
      }
}
