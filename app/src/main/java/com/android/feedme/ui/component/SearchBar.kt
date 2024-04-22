package com.android.feedme.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Composable function for the Search Bar.
 *
 * This function displays the search bar on both the landing and saved pages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarFun() {

  var query by remember { mutableStateOf("") }
  var active by remember { mutableStateOf(false) }

  // Search bar + filters icon
  Row(modifier = Modifier.background(Color.White), verticalAlignment = Alignment.CenterVertically) {
    SearchBar(
        query = query,
        active = active,
        onActiveChange = { active = it },
        onQueryChange = { query = it },
        onSearch = {
          active = false
          /* TODO() add filtering logic here */
        },
        leadingIcon = {
          Icon(
              Icons.Default.Menu,
              "Menu Icon",
              modifier = Modifier.testTag("FilterClick").clickable {})
        },
        trailingIcon = {
          if (active) {
            Icon(
                modifier =
                    Modifier.clickable {
                      query = ""
                      active = false
                      /* TODO() add filtering logic here */
                    },
                imageVector = Icons.Default.Close,
                contentDescription = "Close Icon")
          } else {
            Icon(Icons.Default.Search, contentDescription = "Search Icon")
          }
        },
        placeholder = { Text("Search Recipe") },
        modifier = Modifier.fillMaxWidth().padding(10.dp).height(50.dp).testTag("SearchBar")) {}
  }
}
