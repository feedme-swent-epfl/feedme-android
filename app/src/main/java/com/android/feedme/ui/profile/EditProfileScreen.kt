package com.android.feedme.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.model.data.Profile
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route


@Composable
fun EditProfileScreen( navigationActions: NavigationActions, profileViewModel: ProfileViewModel= ProfileViewModel()) {
    val profile = profileViewModel.profile.collectAsState().value ?: Profile()

    var name by remember { mutableStateOf(profile.name) }
    var username by remember { mutableStateOf(profile.username) }
    var bio by remember { mutableStateOf(profile.description) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        TextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            maxLines = 4,
            textStyle = TextStyle(fontSize = 14.sp),
            modifier = Modifier.height(150.dp)
        )
        Button(onClick = {

            val updatedProfile = profile.copy(name = name, username = username, description = bio)
            profileViewModel.setProfile(updatedProfile)
            navigationActions.navigateTo(Route.PROFILE)

        }){
            Text("Save Changes")
        }
    }
}
