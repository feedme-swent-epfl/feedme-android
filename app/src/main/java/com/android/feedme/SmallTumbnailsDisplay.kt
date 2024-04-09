package com.android.feedme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import com.android.feedme.model.data.Recipe
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Step


@Composable
fun SmallThumbnailsDisplay(listRecipe : List<Recipe>){
    val imageWidth = LocalConfiguration.current.screenWidthDp / 2
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = imageWidth.dp)) {
        items(listRecipe.size){i ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = listRecipe[i].title)
            }

        }
    }
    /*Column{
        listRecipe.chunked(2) { rowItems ->
            RowWithItems(rowItems)
        }
    }*/
}



@Composable
fun RecipeAloneDisplay(recipe : Recipe){

}

//@Preview
@Composable
fun PreviewSmallThumbnailsDisplay(){
    val testRecipes : List<Recipe> = listOf(
        Recipe(recipeId = "lasagna1", title = "Tasty Lasagna", description = "a",
            ingredients = listOf(IngredientMetaData(quantity = 2.0 ,measure = MeasureUnit.ML,
                ingredient = Ingredient("Tomato","Vegetables", "tomatoID"))),
            steps = listOf(Step(1,"a","Step1")), tags =  listOf("Meat"),
            time = 1.15,rating = 4.5, userid = "PasDavid", difficulty = "Intermediate",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images"
        ))
    SmallThumbnailsDisplay(listRecipe = testRecipes)
    //faire une instance de my component

}

