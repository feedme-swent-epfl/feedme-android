package com.android.feedme.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.model.viewmodel.HomeViewModel

/**
 * Composable function for the Search Bar.
 *
 * This function displays the search bar on both the landing and saved pages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarFun(viewModel: HomeViewModel) {
  var query by remember { mutableStateOf(viewModel.initialSearchQuery) }
  var active by remember { mutableStateOf(false) }

    // Action to be performed when searching
    val onSearch: (String) -> Unit = {
        active = false
        // Execute search logic only if the query is not empty
        if (query.isNotEmpty()) {
            viewModel.resetSearch()
            viewModel.searchProfiles(it)
            viewModel.searchRecipes(it)
            viewModel.isFiltered = true
        }
    }

  // Search bar + filters icon
  Row(modifier = Modifier.background(Color.White), verticalAlignment = Alignment.CenterVertically) {
    SearchBar(
        modifier = Modifier.fillMaxWidth().padding(10.dp).height(53.dp).testTag("SearchBar"),
        query = query,
        active = active,
        onActiveChange = { active = it },
        onQueryChange = { query = it },
        onSearch = onSearch,
        leadingIcon = {
            if (active) {
              IconButton(
                  onClick = {onSearch(query)}
              ) {
                  Icon(
                      Icons.Default.Search,
                      contentDescription = "Search Icon Button",
                      modifier = Modifier.size(26.dp)
                  )
              }
            } else {
                IconButton(onClick = { /*TODO: add manual filtering logic*/ }) {
                    Icon(
                        Icons.Outlined.Tune,
                        contentDescription = "Filter Icon",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        trailingIcon = {
          if (active) {
            IconButton(
                onClick = {
                  query = ""
                  active = false
                  viewModel.resetSearch()
                    viewModel.isFiltered = false
                }) {
                  Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Close Icon",
                      modifier = Modifier.size(26.dp))
                }
          } else {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search Icon",
                modifier = Modifier.size(26.dp))
          }
        },
        placeholder = {
          Text(
              text = "Find a recipe or friend",
              fontStyle = FontStyle.Italic,
              fontSize = 16.sp,
              modifier = Modifier.testTag("Placeholder Text"))
        },
        content = {
          Column(modifier = Modifier.fillMaxHeight().background(Color.Black)) {
            Text("Hello World")
          }
        })
  }
}
