package com.android.feedme.ui.component

import android.text.Layout
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.ui.theme.TemplateColor

/**
 * Composable function to enable comment creation
 * */
@Preview
@Composable
fun CreateComment() {

    var commentTitle by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Transparent)
            .testTag("OuterBox"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(350.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(2.dp, TemplateColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
                .testTag("InnerCol"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Picture + title + rating and time
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.Left,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clickable plus icon -- temporary until i implement the picture uploading process
                Box(
                    Modifier
                        .size(50.dp)
                        .background(Color.LightGray, CircleShape)
                        .clickable { /* TODO() onImageUpload() */ }
                        .testTag("PhotoIcon"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.padding(end = 10.dp))

                Column (
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Title input
                    OutlinedTextField(
                        value = commentTitle,
                        onValueChange = { commentTitle = it },
                        label = { Text("Enter the title of the comment") },
                        modifier = Modifier.fillMaxWidth().testTag("TitleField"),
                        shape = RoundedCornerShape(20.dp)
                    )

                    // Rating input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp),
                        horizontalArrangement = Arrangement.Absolute.Right,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // the input
                        TextField(
                            value = rating,
                            onValueChange = { rating = it },
                            modifier = Modifier
                                .width(20.dp)
                                .height(30.dp)
                                .background(color = Color.LightGray)
                                .testTag("RatingField"),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(cursorColor = Color.Black))
                        // rating icon
                        Icon(
                            imageVector = Icons.Outlined.StarOutline,
                            contentDescription = "RatingIcon",
                            modifier = Modifier
                                .size(34.dp)
                                .padding(start = 6.dp)
                                .testTag("RatingStar"))
                    }
                }
            }

            // Description input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("DescriptionField"),
                shape = RoundedCornerShape(16.dp)
            )

            // Delete and Publish buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // delete button
                OutlinedButton(
                    onClick = { /* TODO() implement deleting button functionality */ },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    modifier =
                    Modifier
                        .width(150.dp)
                        .height(48.dp)
                        .border(2.dp, Color.Red, shape = RoundedCornerShape(20.dp))
                        .testTag("DeleteButton"),
                    shape = RoundedCornerShape(20.dp)) {
                    Text(
                        "Delete",
                        color = Color.Red,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // publish button
                OutlinedButton(
                    onClick = { /* TODO() implement deleting button functionality */ },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    modifier =
                    Modifier
                        .width(150.dp)
                        .height(48.dp)
                        .border(2.dp, TemplateColor, shape = RoundedCornerShape(20.dp))
                        .testTag("PublishButton"),
                    shape = RoundedCornerShape(20.dp)) {
                    Text(
                        "Publish",
                        color = TemplateColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

            }


        }
    }
}