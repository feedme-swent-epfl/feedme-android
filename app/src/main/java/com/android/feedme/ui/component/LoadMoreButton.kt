package com.android.feedme.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun LoadMoreButton(loadMore: () -> Unit) {
  OutlinedButton(onClick = { loadMore() }, shape = RoundedCornerShape(20.dp)) { Text("Load More") }
}
