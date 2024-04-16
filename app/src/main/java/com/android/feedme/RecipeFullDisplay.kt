package com.android.feedme

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.BlueUser
import com.android.feedme.ui.theme.YellowStar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipeFullDisplay(recipe: Recipe, modifier: Modifier = Modifier) {
  Scaffold(
      topBar = {
        TopBarNavigation(
            title = recipe.title,
            navAction = null,
            rightIcon = null,
            rightIconOnClickAction = { null })
      },
      bottomBar = {
        BottomNavigationMenu(selectedItem = "", onTabSelect = {}, tabList = TOP_LEVEL_DESTINATIONS)
      }) {
        LazyColumn(contentPadding = PaddingValues(all = 0.dp)) {
            item{ ImageDisplay(recipe = recipe, modifier = modifier)}
            item{ GeneralInfosDisplay(recipe = recipe, modifier = modifier)}
        }
      }
}

@Composable
fun ImageDisplay(recipe: Recipe, modifier: Modifier){
    AsyncImage(model = recipe.imageUrl,
        contentDescription = "Recipe Image",
        modifier = modifier.fillMaxWidth())
}
@Composable
fun GeneralInfosDisplay(recipe: Recipe, modifier: Modifier){
    Row(horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(50.dp)){
        // Recipe time
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Rounded.AccessTime, contentDescription = null)
        Text(text = recipe.time.toString(),
            modifier = Modifier.padding(start = 4.dp),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold))
        // Recipe user name
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "By user ",
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold))
        Text(text = recipe.userid,
            textAlign = TextAlign.Center,
            color = BlueUser,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.weight(1f))
        // Recipe rating
        Icon(imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = YellowStar)
        Text(text = recipe.rating.toString(),
            modifier = Modifier.padding(start = 4.dp),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.weight(1f))
    }
    HorizontalDivider(thickness = 2.dp)
}

@Composable
fun UstentilsDisplay(){

}

@Preview
@Composable
fun Preview(){
    val recipe1 =
        Recipe(
            recipeId = "lasagna1",
            title = "Tasty Lasagna",
            description = "a",
            ingredients =
            listOf(
                IngredientMetaData(
                    quantity = 2.0,
                    measure = MeasureUnit.ML,
                    ingredient = Ingredient("Tomato", "Vegetables", "tomatoID")
                )
            ),
            steps = listOf(Step(1, "a", "Step1")),
            tags = listOf("Meat"),
            time = 1.15,
            rating = 4.5,
            userid = "@PasDavid",
            difficulty = "Intermediate",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")
    RecipeFullDisplay(recipe = recipe1, modifier = Modifier)
}
