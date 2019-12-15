package io.fluidsonic.graphql


class GFragmentDefinition(
	val name: String,
	val typeCondition: GNamedTypeRef,
	val selectionSet: GSelectionSet,
	val directives: List<GDirective> = emptyList()
) {

	companion object {

		// FIXME validation
		fun from(ast: GAst.Definition.Fragment) =
			GFragmentDefinition(
				directives = ast.directives.map { GDirective.from(it) },
				name = ast.name.value,
				typeCondition = GNamedTypeRef.from(ast.typeCondition),
				selectionSet = GSelectionSet.from(ast.selectionSet)
			)
	}
}
