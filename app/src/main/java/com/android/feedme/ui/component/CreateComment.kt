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
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CreateComment() {
    var commentTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(4.0f) }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Gray.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Plus icon
            Box(
                Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            // Title input
            OutlinedTextField(
                value = commentTitle,
                onValueChange = { commentTitle = it },
                label = { Text("Enter the title of the Comment") },
                modifier = Modifier.fillMaxWidth()
            )

            // Time and rating indicators
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("~ 15’", fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("4.0", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                }
            }

            // Description input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // Buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { /* Handle Delete */ }, colors = ButtonDefaults.buttonColors(Color.Red)) {
                    Text("Delete")
                }
                Button(onClick = { /* Handle Publish */ }, colors = ButtonDefaults.buttonColors(Color.Blue)) {
                    Text("Publish", color = Color.White)
                }
            }
        }
    }
}

@Preview
@Composable
fun Test() {

    var commentTitle by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(4.0f) }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Gray.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(350.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp),
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
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                        .clickable { /* TODO() onImageUpload()*/ }, // Trigger the image upload function
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.padding(end = 10.dp))

                Column (
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Title input
                    OutlinedTextField(
                        value = commentTitle,
                        onValueChange = { commentTitle = it },
                        label = { Text("Enter the title of the comment") },
                        modifier = Modifier.fillMaxWidth(), //.padding(bottom = 12.dp),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Estimated Time input
                        Row(
                            modifier = Modifier.padding(horizontal = 40.dp).background(),
                            verticalAlignment = Layout.Alignment.CenterVertically
                        ) {
                            // the input
                            TextField(
                                value = estimatedTime,
                                onValueChange = { estimatedTime = it },
                                modifier = Modifier.width(20.dp).height(10.dp).background(color = Color.LightGray)
                            )
                            // time icon
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(34.dp).padding(start = 4.dp))

                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Rating input
                        Column(
                            modifier = Modifier
                                .width(10.dp)
                                .height(10.dp)
                        ) {
                            Text("Rating: %.1f".format(rating), fontSize = 16.sp)
                            Slider(
                                value = rating,
                                onValueChange = { rating = it },
                                valueRange = 0f..5f,
                                steps = 4, // Divide into 0.5-star increments
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

            }
        }
    }

}