package com.android.feedme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.TopBarColor

@Composable
fun TopBarNavigation(
    title: String,
    navController: NavController? = null,
    rightIcon: ImageVector? = null,
    rightIconOnClickAction: (() -> Unit) = {}
) {
  Box(
      modifier = Modifier.fillMaxWidth().testTag("TopBarNavigation").background(TemplateColor),
      contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {

              // LeftIconBox
              Box(
                  modifier = Modifier.weight(1f).testTag("LeftIconBox"),
                  contentAlignment = Alignment.CenterStart) {
                    if (navController != null) {
                      if (navController.previousBackStackEntry != null) {
                        IconButton(
                            modifier = Modifier.testTag("LeftIconButton"),
                            onClick = { navController.popBackStack() },
                        ) {
                          Icon(
                              modifier = Modifier.testTag("LeftIcon"),
                              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                              contentDescription = "Back",
                              tint = TopBarColor)
                        }
                      }
                    }
                  }

              // TitleBox
              Box(
                  modifier = Modifier.weight(1f).testTag("TitleBox"),
                  contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier.testTag("TitleText"),
                        text = title,
                        fontSize = 20.sp,
                        color = TopBarColor,
                        textAlign = TextAlign.Center)
                  }

              // RightIconBox
              Box(
                  modifier = Modifier.weight(1f).testTag("RightIconBox"),
                  contentAlignment = Alignment.CenterEnd) {
                    if (rightIcon != null) {
                      IconButton(
                          onClick = { rightIconOnClickAction },
                          modifier = Modifier.testTag("RightIconButton")) {
                            Icon(
                                modifier = Modifier.testTag("RightIcon"),
                                imageVector = rightIcon,
                                contentDescription = "Save",
                                tint = TopBarColor)
                          }
                    }
                  }
            }
      }
}

