package io.fluidsonic.graphql


interface Visit {

	val isAborting: Boolean
	val isSkippingChildren: Boolean

	fun abort()
	fun skipChildren()
	fun visitChildren()


	@Suppress("FunctionName")
	fun __unsafeVisitChildren(data: Any?) // FIXME How to make this generic with type projection issues in the Visitor?
}