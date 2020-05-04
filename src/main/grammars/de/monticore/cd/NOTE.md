# Split of the CD language in different parts

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
- CDType has a list of member
- add default values to method parameter
- add ordered flag to `CDAssociationSide`


## CDBasis.mc4
* `CDCompilationUnit`
  * `targetpackage` describes the package to use in a generator,
    * if _existent_ the `targetpackage` should be used
    * if _not existent_, the `package` should be used 
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
* how to add `implements` to `CDClass`?
  * override the terminal
  * define a terminal, which does nothing, but can be overriden:
  ```
  CDInterfaceUsage;
  
  scope CDClass implements CDType = CDModifier "class" Name
      ( "extends"   superclass:(MCObjectType || ",")+ )?
      CDInterfaceUsage
      ( "{"
          CDMember*
        "}"
      | ";" );
  ```
* CDTypeSymbol
 * how to add `isInterface`, `isEnum`, `cdInterfaces` later?
* use Symbol instead of SymbolLoader in the symbols
* use TypeSymbols (can only be done when a symbol can derive multiple symbols)
  * `Type` for `CDType` or `CDClass`
  * `Field` for `CDAttribute`
  * `Method` for `CDMethod`
* write `CDTypeSymbol` for `CDCommon` to include interfaces

### RTE
* add optional CoCo for restriction of multiple inheritance
* add visitor for CDCardinality, which can then be overriden, when additional
  cardinalities are defined
* write SymModifier which contains methods to check each of the modifier


# Future
- target imports (for generated code)