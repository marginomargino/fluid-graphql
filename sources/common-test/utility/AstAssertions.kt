package tests

import io.fluidsonic.graphql.*
import kotlin.test.*


// expected to not throw
fun assertAst(actual: String) =
	GDocument.parse(makeSource(actual.trimMargin()))


// Note that "start .. end" origin notations use an exclusive end rather than an inclusive for sake of readability
@Suppress("NAME_SHADOWING")
fun assertAst(actual: String, expected: AstBuilder.() -> GAst) {
	val expected = ast(expected)
	val actual = GDocument.parse(makeSource(actual.trimMargin()))

	assertTrue(
		actual = actual.equalsAst(expected, includingOrigin = true),
		message = "Expected <$expected>, actual <$actual>."
	)
}


inline fun <T : GAst> List<T>.assertMany(count: Int, block: List<T>.() -> Unit) {
	assertEquals(expected = count, actual = size)

	block()
}


inline fun <T : GAst> List<T>.assertOne(block: T.() -> Unit) =
	assertMany(1) {
		single().apply(block)
	}


inline fun <reified T : GAst> GAst.assertClass(block: T.() -> Unit) {
	assertTrue(this is T, "Expected '$this' to be of ${T::class}")

	block(this)
}


fun GNamedType.assertName(name: String, range: IntRange) {
	assertEquals(expected = name, actual = this.name)
	nameNode.assertAt(range)
}


fun assertSyntaxError(
	content: String,
	message: String,
	line: Int,
	column: Int
) {
	val error = assertFailsWith<GError> { GDocument.parse(content.trimMargin()) }
	assertEquals(expected = message, actual = error.message)
	assertEquals(expected = line, actual = error.origins.first().line, message = "incorrect line")
	assertEquals(expected = column, actual = error.origins.first().column, message = "incorrect column")
}


private fun makeSource(content: String) =
	object : GSource.Parsable {

		override val content = content
		override val name = "<test>"


		override fun makeOrigin(startPosition: Int, endPosition: Int, column: Int, line: Int) =
			AstBuilder.Origin(
				startPosition = startPosition,
				endPosition = endPosition,
				line = line,
				column = column
			)
	}


////////////

fun GAst.assertAt(range: IntRange) {
	val origin = origin

	assertNotNull(origin) { ".origin" }
	assertEquals(expected = origin.startPosition .. origin.endPosition, actual = range)
}


fun GDefinition.asOperation() =
	this as GOperationDefinition


inline fun <T : GAst> T?.assert(block: T.() -> Unit) {
	assertNotNull(this)

	block()
}


inline fun <reified T : GAst> GAst?.assertOf(block: T.() -> Unit) {
	assertNotNull(this)
	assertTrue { this is T }

	(this as T).block()
}


inline fun <reified T : GAst> List<*>.assertOneOf(block: T.() -> Unit) {
	assertEquals(expected = 1, actual = size)

	val single = single()
	assertTrue { single is T }

	(single as T).block()
}


fun GSelection.asFieldSelection() =
	this as GFieldSelection


fun GValue.asString() =
	this as GValue.String