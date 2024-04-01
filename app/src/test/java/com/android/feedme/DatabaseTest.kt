import com.android.feedme.model.data.Ingredient
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

@RunWith(MockitoJUnitRunner::class)
class DatabaseTest {

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var documentReference: DocumentReference

    private lateinit var database: FirestoreDatabase

    @Before
    fun setUp() {
        // Assuming FirestoreDatabase constructor takes FirebaseFirestore as an argument
        // Adjust this based on your actual FirestoreDatabase class implementation
        database = FirestoreDatabase()
    }

    @Test
    fun addIngredient_addsIngredientSuccessfully() {
        // Given
        val ingredient = Ingredient("Salt", "Condiment", "1")
        `when`(firestore.collection("ingredients").document(ingredient.id)).thenReturn(documentReference)
        `when`(documentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate a successful operation

        var successCalled = false

        // When
        database.addIngredient(ingredient, { successCalled = true }, { /* Handle failure */ })

        // Then
        verify(documentReference).set(any()) // Verify set was called with any HashMap
        assertTrue(successCalled)
    }
}
