package com.android.feedme.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.TextBarColor

/**
 * TopBarNavigation is a composable function used to display a top navigation bar. The left icon
 * (which is a back arrow) will appear only if the navAction can pop back; otherwise, it won't be
 * displayed, even if it isn't null.
 *
 * @param title The title text to be displayed in the center of the top bar.
 * @param navAction The navigation action instance for handling back navigation. Default is null.
 * @param rightIcon The icon to be displayed on the right side of the top bar. Default is null.
 * @param rightIconOnClickAction The action to be performed when the right icon is clicked. Default
 *   is Unit. No action is taken if rightIcon is null.
 */
@Composable
fun TopBarNavigation(
    title: String,
    navAction: NavigationActions? = null,
    backArrowOnClickAction: () -> Unit = { navAction?.goBack() },
    rightIcon: ImageVector? = null,
    rightIconOnClickAction: (() -> Unit) = {}
) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(60.dp)
              .testTag("TopBarNavigation")
              .background(TemplateColor),
      contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {

              // LeftIconBox
              Box(
                  modifier = Modifier.weight(1f).testTag("LeftIconBox"),
                  contentAlignment = Alignment.CenterStart) {
                    if (navAction != null && navAction.canGoBack()) {
                      IconButton(
                          modifier = Modifier.testTag("LeftIconButton"),
                          onClick = { backArrowOnClickAction() },
                      ) {
                        Icon(
                            modifier = Modifier.testTag("LeftIcon"),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "BackArrow",
                            tint = TextBarColor)
                      }
                    }
                  }

              // Title
              Text(
                  modifier = Modifier.testTag("TitleText").width(250.dp),
                  text = title,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextBarColor,
                  textAlign = TextAlign.Center)

              // RightIconBox
              Box(
                  modifier = Modifier.weight(1f).testTag("RightIconBox"),
                  contentAlignment = Alignment.CenterEnd) {
                    if (rightIcon != null) {
                      IconButton(
                          onClick = { rightIconOnClickAction() },
                          modifier = Modifier.testTag("RightIconButton")) {
                            Icon(
                                modifier = Modifier.testTag("RightIcon"),
                                imageVector = rightIcon,
                                contentDescription = "Right Icon",
                                tint = TextBarColor)
                          }
                    }
                  }
            }
      }
}
