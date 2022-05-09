package ltd.toktok.tox4j

import kotlin.test.Test
import kotlin.test.assertTrue

public class Tox4jTestSuite {
    @Test public fun someLibraryMethodReturnsTrue() {
        val classUnderTest = Library()
        assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'")
    }
}
