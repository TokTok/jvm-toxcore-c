package ltd.toktok.tox4j

import kotlin.test.Test
import kotlin.test.assertTrue

class Tox4jTestSuite {
    @Test fun someLibraryMethodReturnsTrue() {
        val classUnderTest = Library()
        assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'")
    }
}
