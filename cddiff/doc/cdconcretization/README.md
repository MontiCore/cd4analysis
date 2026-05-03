# CD Completion

CD Completion automatically extends an incomplete *concrete* Class Diagram so that it
conforms to a *reference* Class Diagram. The entry point is
`de.monticore.cdconcretization.ConcretizationCompleter`.

## Key Concepts

| Concept | Description |
|---|---|
| **Reference CD** | A CD that defines the structure a concrete CD must implement. |
| **Concrete CD** | An incomplete CD that is extended by the completer. |
| **Incarnation** | A concrete element that implements a reference element. |
| **Mapping** | A named set of stereotype-encoded incarnation bindings (e.g., `<<ref="...">>` or `<<m1="...">>`). |

## Configuration Parameters (`CDConfParameter`)

The completer and conformance checker share a set of parameters that control matching and
adaptation behaviour. Parameters relevant to completion are listed below.

| Parameter | Effect |
|---|---|
| `STEREOTYPE_MAPPING` | Incarnations are identified by explicit `<<mappingName="refElement">>` stereotypes. |
| `NAME_MAPPING` | Incarnations are identified by equal element names. |
| `METHOD_OVERLOADING` | Method incarnations are matched by full signature (name + parameter types), not just name. |
| `STRICT_PARAMETER_ORDER` | Method parameters are matched by type in strict positional order, without requiring name equality. Without this, parameters must also match by name. |
| `INHERITANCE` | Attributes, methods, and associations required by a reference type may be defined in a supertype of the concrete incarnation type, rather than in the incarnation type itself. |
| `SRC_TARGET_ASSOC_MAPPING` | Association incarnations are matched by source type and target role name. |
| `ADAPTED_NAME_MAPPING` | Enables name-adapted incarnation matching: a concrete element whose name is the type-incarnation-adapted form of a reference element name is recognised as its incarnation — without requiring an explicit stereotype. See [Implicit Name Adaptation](feature-implicit-name-adaptation.md). |

## Feature Documentation

- [Implicit Name Adaptation](feature-implicit-name-adaptation.md) — how element names are
  automatically adapted during completion based on type incarnation pairs, and how
  `ADAPTED_NAME_MAPPING` affects stereotype generation.

## Related Design / Planning Notes

- [Implicit Name Adaptation (design notes)](implicit-name-adaptation.md)
- [Name Adaptation Improvements (future work)](name-adaptation-improvements.md)
