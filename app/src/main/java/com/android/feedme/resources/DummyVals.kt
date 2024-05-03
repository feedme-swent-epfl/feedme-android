package com.android.feedme.resources

import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step

val recipe =
    Recipe(
        recipeId = "lasagna1",
        title = "Tasty Lasagna",
        description =
            "Description of the recipe, writing a longer one to see if it fills up the whole space available. Still writing with no particular aim lol",
        ingredients =
            listOf(
                IngredientMetaData(
                    quantity = 2.0,
                    measure = MeasureUnit.ML,
                    ingredient = Ingredient("Tomato", "Vegetables", "tomatoID"))),
        steps =
            listOf(
                Step(
                    1,
                    "In a large, heavy pot, put the olive oil, garlic and parsley over medium high heat. When the garlic begins to brown, increase the heat and add the ground beef. Break up the beef, but keep it rather chunky. Sprinkle with about 1/2 tsp of salt. \n" +
                        "\n" +
                        "When the beef is beginning to dry up, add the tomatoes and stir well. Add more salt, then lower the heat and allow to simmer for about an hour, stirring from time to time. Taste for salt and add pepper.",
                    "Make the Meat Sauce")),
        tags = listOf("Meat"),
        time = 45.0,
        rating = 4.5,
        userid = "username",
        difficulty = "Intermediate",
        "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")