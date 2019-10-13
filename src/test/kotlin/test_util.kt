import kotlin.test.assertTrue

fun <T> assertEmpty(c: Collection<T>, message: String? = null) {
    assertTrue(c.isEmpty(), message ?: "Expected empty collection, got: $c")
}

fun <T> assertEmpty(i: Iterable<T>, message: String? = null) = assertEmpty(i.toList(), message)

fun <T> assertNotEmpty(c: Collection<T>, message: String? = null) {
    assertTrue(c.isNotEmpty(), message ?: "Expected non-empty collection")
}

fun <T> assertNotEmpty(i: Iterable<T>, message: String? = null) = assertNotEmpty(i.toList(), message)