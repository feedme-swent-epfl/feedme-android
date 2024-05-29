package com.android.feedme.resources

import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step

val recipe1 =
    newRecipe(
        "lasagna1",
        "Banana Cake",
        "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Firepo.primecp.com%2F2019%2F09%2F422594%2FEasy-Banana-Bread_UserCommentImage_ID-3361638.jpg%3Fv%3D3361638&f=1&nofb=1&ipt=5bc64da42de6992a32947432048f5647017ef9b6a9727960a5ef305cf5afc2bf&ipo=images")
val recipe2 =
    newRecipe(
        "pasta1",
        "Pasta Bolognaise",
        "https://www.eatwell101.com/wp-content/uploads/2022/05/Beef-Pasta-in-Tomato-Sauce.jpg")

fun newRecipe(recipeId: String, title: String, url: String): Recipe {
  return Recipe(
      recipeId = recipeId,
      title = title,
      description =
          "Description of the recipe, writing a longer one to see if it fills up the whole space available. Still writing with no particular aim lol",
      ingredients =
          listOf(
              IngredientMetaData(
                  quantity = 2.0,
                  measure = MeasureUnit.ML,
                  ingredient = Ingredient("Tomato", "Vegetables", false, false))),
      steps =
          listOf(
              Step(
                  1,
                  "In a large, heavy pot, put the olive oil, garlic and parsley over medium high heat. When the garlic begins to brown, increase the heat and add the ground beef. Break up the beef, but keep it rather chunky. Sprinkle with about 1/2 tsp of salt. \n" +
                      "\n" +
                      "When the beef is beginning to dry up, add the tomatoes and stir well. Add more salt, then lower the heat and allow to simmer for about an hour, stirring from time to time. Taste for salt and add pepper.",
                  "Make the Meat Sauce")),
      tags = listOf("Meat"),
      rating = 4.7,
      userid = "9vu1XpyZwrW5hSvEpHuuvcVVgiv2",
      imageUrl = url)
}
