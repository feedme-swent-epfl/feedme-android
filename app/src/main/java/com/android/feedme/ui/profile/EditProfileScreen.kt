package com.android.feedme.ui.profile

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.model.data.Profile
import com.android.feedme.model.viewmodel.CameraViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

/**
 * Composable function that provides the main screen layout for editing a user's profile.
 *
 * This function sets up the Scaffold layout which includes a top bar, bottom navigation, and the
 * content for editing the profile, handled by [EditProfileContent].
 *
 * @param navigationActions A set of actions to handle navigation events.
 * @param profileViewModel The ViewModel that handles fetching and updating the user's profile data.
 */
@Composable
fun EditProfileScreen(navigationActions: NavigationActions, profileViewModel: ProfileViewModel) {

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("EditProfileScreen"),
      topBar = { TopBarNavigation(title = "Edit Profile", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(Route.PROFILE, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding -> EditProfileContent(padding, profileViewModel, navigationActions) },
  )
}

/**
 * Composable function that displays and manages the profile editing form.
 *
 * This function creates a form where users can edit their profile details such as name, username,
 * and bio. The form validates inputs and shows errors when necessary. It allows the user to save
 * the changes which updates the profile information in the backend via the [profileViewModel].
 *
 * @param padding The padding to apply around the content area defined by the Scaffold in
 *   [EditProfileScreen].
 * @param profileViewModel ViewModel that contains the logic for loading and saving the user's
 *   profile.
 * @param navigationActions Actions to handle navigation based on user interaction.
 */
@Composable
fun EditProfileContent(
    padding: PaddingValues,
    profileViewModel: ProfileViewModel,
    navigationActions: NavigationActions
) {
  val profile = profileViewModel.currentUserProfile.collectAsState().value ?: Profile()

  val cameraViewModel = viewModel<CameraViewModel>()
  val pickImage = cameraViewModel.galleryLauncher(profileViewModel, null, null)

  var name by remember { mutableStateOf(profile.name) }
  var username by remember { mutableStateOf(profile.username) }
  var bio by remember { mutableStateOf(profile.description) }
  var nameError by remember { mutableStateOf<String?>(null) }
  var usernameError by remember { mutableStateOf<String?>(null) }
  var bioError by remember { mutableStateOf<String?>(null) }

  val minNameSize = 3
  val maxNameSize = 15
  val bioMaxSize = 100

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
          Modifier.padding(24.dp).padding(padding).fillMaxSize().testTag("EditProfileContent")) {
        Column(
            modifier =
                Modifier.padding(bottom = 20.dp, top = 20.dp).testTag("Edit_Picture").clickable {
                  pickImage.launch(
                      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
        ) {
          UserProfilePicture(profileViewModel)
          Text(
              text = "Edit Picture",
              style = TextStyle(fontSize = 16.sp),
              modifier = Modifier.padding(top = 10.dp).align(Alignment.CenterHorizontally))
        }

        OutlinedTextField(
            value = name,
            onValueChange = {
              name = it
              nameError =
                  when {
                    it.isBlank() -> "Name cannot be empty"
                    it.length < minNameSize -> "Name must be at least $minNameSize characters"
                    it.length > maxNameSize -> "Name must be no more than $maxNameSize characters"
                    else -> null
                  }
            },
            label = { Text("Name") },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).testTag("NameInput"))
        if (nameError != null) {
          Text(
              nameError!!,
              color = MaterialTheme.colorScheme.error,
              style = TextStyle(fontSize = 12.sp),
              modifier = Modifier.testTag("NameError"))
        }

        OutlinedTextField(
            value = username,
            onValueChange = {
              username = it
              usernameError =
                  when {
                    it.isBlank() -> "Username cannot be empty"
                    it.contains(" ") -> "Username cannot contain spaces"
                    !it.matches("^[\\w_]*$".toRegex()) ->
                        "Username must be alphanumeric or underscores"
                    it.length < minNameSize -> "Username must be at least $minNameSize characters"
                    it.length > maxNameSize ->
                        "Username must be no more than $maxNameSize characters"
                    else -> null
                  }
            },
            label = { Text("Username") },
            isError = usernameError != null,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).testTag("UsernameInput"))

        if (usernameError != null) {
          Text(
              usernameError!!,
              color = MaterialTheme.colorScheme.error,
              style = TextStyle(fontSize = 12.sp),
              modifier = Modifier.testTag("UsernameError"))
        }
        OutlinedTextField(
            value = bio,
            onValueChange = {
              bio = it
              bioError =
                  if (it.length > bioMaxSize) "Bio must be no more than $bioMaxSize characters"
                  else null
            },
            label = { Text("Bio") },
            isError = bioError != null,
            singleLine = false,
            modifier =
                Modifier.fillMaxWidth().height(125.dp).padding(top = 4.dp).testTag("BioInput"))

        if (bioError != null) {
          Text(
              bioError!!,
              color = MaterialTheme.colorScheme.error,
              style = TextStyle(fontSize = 12.sp),
              modifier = Modifier.testTag("BioError"))
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).testTag("EditSave"),
            onClick = {
              if (nameError == null &&
                  usernameError == null &&
                  name.isNotEmpty() &&
                  username.isNotEmpty() &&
                  profileViewModel._imageUrl.value != null) {
                profileViewModel.setProfile(
                    Profile(
                        profile.id,
                        name,
                        username,
                        profile.email,
                        bio,
                        profileViewModel._imageUrl.value!!,
                        profile.followers,
                        profile.following,
                        profile.filter,
                        profile.recipeList,
                        profile.savedRecipes,
                        profile.commentList))
                navigationActions.navigateTo(Route.PROFILE)
              }
            },
            enabled =
                name.isNotEmpty() &&
                    username.isNotEmpty() &&
                    nameError == null &&
                    usernameError == null) {
              Text("Save")
            }
      }
}
