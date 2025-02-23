<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

# SymTabDefinition

MontiCore Symbol tables are stored as JSON artifacts.
JSON symbol tables are not very concise, easy to read or write.
As such, JSON symbol tables are best generated
based on a model by a language tool.

[**SymTabDefinition**](SymTabDefinition.mc4) is a small, non-conservative
[CD4Code](cd4analysis.md#cd4code) variant for the modelling of symbol tables.
Its models a concise, easily read- and writeable representation
of symbol tables.
The Current version focuses on types, functions, and variables.
As its sole purpose is to describe symbol tables,
elements that are not represented in the symbol table
are not part of the language,
e.g., initial values of variables/fields.

## Example for SymTabDefinition

```
package de.monticore.math;

symtabdefinition Calc {
  
  double pi; 
  
  int abs(int i);
  
  class Vec2<T> {
    + T v1;
    + T v2;
    + (T, T) asTuple();
    + <U> Vec2<U> map(T -> U mapping);
  }

}
```

The example:
- defines a variable symbol `pi` of type `double`.
- defines a function symbol `abs` of type `int -> int`;
    - the parameter has name `i`.
- defines a (generic) CDType symbol `Vec2`;
  In the spanned scope of the symbol are
    - a type-variable symbol `T`.
    - two public field symbols `v1`, `v1`, each of type `T`.
    - a public method symbol `asTuple` of type `() -> (T, T)`.
    - a public generic method symbol `map` of type `(T -> U) -> Vec2<U>`;
        - the function parameter has the name `mapping`;
        - the type parameter has the name `U`;

## Differences between SymTabDefinition and CD4Code

CD4Code has model elements that are not part of the SymTabDefinition Language
 - initial values for fields (and variables) are not stored in the symbol table,
   and as such are not part of the language.
 - Associations are not part of the SymTabDefinition language.

SymTabDefinition has model elements that are not part of CD4Code
 - top level variables/functions
 - [Function types][MCFunctionTypesGrammar], e.g., `int -> int`
 - [Union, intersection, and tuple types][MCStructuralTypesGrammar], e.g.,
   `Student | Teacher`, `Car & Ship`, `(int, int)`

[MCFunctionTypesGrammar]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCFunctionTypes.mc4
[MCStructuralTypesGrammar]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCStructuralTypes.mc4

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/HEAD/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/HEAD/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/HEAD/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/HEAD/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

