package com.android.feedme.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.ui.theme.FabColor

/**
 * Composable function to display a button that loads more content when clicked.
 *
 * @param loadMore: a lambda function that loads more content when the button is clicked
 */
@Composable
fun LoadMoreButton(loadMore: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth().height(70.dp).padding(top = 10.dp, bottom = 20.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(
            modifier = Modifier.fillMaxHeight().width(200.dp).testTag("Load More Button"),
            onClick = { loadMore() },
            shape = RoundedCornerShape(20.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)) {
              Text(
                  text = "Load More",
                  color = FabColor,
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                          fontWeight = FontWeight.SemiBold,
                          letterSpacing = 0.15.sp),
                  modifier = Modifier.testTag("Load More Text"))
            }
      }
}
