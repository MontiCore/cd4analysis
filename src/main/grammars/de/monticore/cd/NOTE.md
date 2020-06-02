# Split of the CD language in different parts

# 28.05.2020
- [ ] auf dev-mergen
- [ ] SymModifier
  - [ ] isAbstract/isStatic werden in neuen TypeSymbols enthalten sein, !aber nur in TypeSymbol
    - soll das dann trotzdem in den SymModifier -> wird beim nächsten Termin ausgemacht
  - [ ] SymModifier in Mill reinlegen: JA
    - [ ] soll es dafür einen Builder geben: JA
    - [ ] wie kann ich dann sauber auf das zugreifen, oder muss ich alle mills überschreiben: in die richtigen Mills schreiben!
- [ ] language.md anpassen
- [ ] mills für alle handgeschriebenen Sachen überschreiben
- [ ] SymTypeArray für ellipse verwenden + setzen in MethodSymbol
- [ ] CD Sprache Version 4.0
- [ ] dependencies aufräumen

- [ ] FieldSymbol
  - [ ] isParameter raus (und zwei listen?)
  - [ ] isConstant rein
- [ ] MethodSymbol
  - [ ] + hasEllipsis

-> keine enum extends other enum: explizite Entscheidung
-> Best practices für namen von (MCType vs type:MCType) besprechen mit DS
-> varargs entscheidung festhalten


This grammar is part of a hierarchy of component grammars, namely
   * cd/CDBasis.mc4
     * cd/CDCommon.mc4
     * cd/Association.mc4
     * cd/CD4CodeBasis.mc4
       * cd/CD4CodeCommon.mc4

And is the base for
   * cd/CD4Analysis.mc4 extends
     * cd/CDBasis.mc4
     * cd/CDCommon.mc4
     * cd/Association.mc4
   * cd/CD4Code.mc4 extends
     * cd/CDBasis.mc4
     * cd/CDCommon.mc4
     * cd/CDAssociation.mc4
     * cd/CD4CodeCommon.mc4

## New since 20.04.2020
- split in different grammars
- add default values to method parameter
- add ordered flag to `CDAssociationSide`


## CDBasis.mc4
* `CDCompilationUnit`
  * `targetpackage` describes the package the modelled classes reside in,
    * if _existent_ the `targetpackage` should be used
    * if _not existent_, the `package` acts as default
* `CDDefinition`
  * contains the all the elements of a classdiagram
* `CDModifier`
  * Base class for all available modifier, is split into `CDDirectModifier`
    and `Stereotype` (originates from `de.monticore.UMLStereotype`)
* interface `CDDirectModifier` is used for all modifiers which are directly
  written to an element in the CD, the following modifiers are defined:
  * `CDAbstractModifier`
  * `CDFinalModifier`
  * `CDStaticModifier`
* interface `CDElement` denotes any element, which is a top-level member of a
  CD
* interface `CDType` denotes all elements, which can be used as a type, only
  `CDClass` is defined here, but other types can be added
* `CDClass` describes a class with
  * its modifiers
* `CDMember` denotes all parts of a CD which can be used as members in
  `CDElement`s
* `CDAttribute`
  * can have a default value as any expression
  * its symbol links to the type and the modifier

## CDCommon.mc4
extends the `cd/CDBasis.mc4` with *Interface*s and *Enum*s


## Overall
* the languages contain keywords of the java language, everything else is no
  grammar keyword

## TODO
### Grammar
* `key`-qualifier cannot be used on anything other than StringLiterals,
  but would be needed to, e.g. describe `[key("association")]`
* CDTypeSymbol
 * how to add `isInterface`, `isEnum`, `cdInterfaces` later?
* use SymTypeExpression instead of SymbolLoader or TypeSymbol in the symbols
* use TypeSymbols (can only be done when a symbol can derive multiple symbols)
  * `Type` for `CDType` or `CDClass`
  * `Field` for `CDAttribute`
  * `Method` for `CDMethod`
* override `CDTypeSymbol` for `CDCommon` to include interfaces
* allow enum to derive from other enum?
* comment like grammar.mc4
* have CDModifier as an interface? order of modifier and stereotype (public <<...>> abstract) should not matter

### CD-MERGE
* what happens, when we combine models, which define different packages?

### RTE
* add optional CoCo for restriction of multiple inheritance
* add visitor for CDCardinality, which can then be overriden, when additional
  cardinalities are defined
* write SymModifier which contains methods to check each of the modifier


# Future
- target imports (for generated code)
