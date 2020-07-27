<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Beta-version: This is intended to become a MontiCore stable explanation. -->

# Class Diagrams (also: UML/P CD)

We provide two versions of UML class diagrams:
- [**CD4Analysis**][CD4AGrammar] is a CD variant for the modelling of data structures
  with classes, attributes, associations, enumerations.
- [**CD4Code**][CD4CGrammar] is an extension of CD4Analysis including methods and constructors.

We additionally provide componenten grammars for parts of the CDs:
- [**CDBasis**][CDBasisGrammar] is the base grammar for all CD languages. It contains the root compilation unit,
  classes, and attributes.
- [**CDInterfaceAndEnum**][CDIAEGrammar] extends CDBasis with interfaces and enums.
- [**CDAssociation**][CDAssocGrammar] defines associations and roles.
- [**CD4CodeBasis**][CD4CBasisGrammar] defines methods and parameters.

# CD4Analysis

The main purpose of this language is the modeling of data structure, which 
typically emerges as result of requirements elicitation activities.

The main grammar file is [`CD4Analysis`][CD4AGrammar].

## Example
```
package de.monticore.life;

classdiagram MyLife { 
  abstract class Person {
    int age;
    Date birthday;
    List<String> nickNames;
  }
  package uni {
    class Student extends Person {
      StudentStatus status;
      -> Address [1..*] {ordered};
    }
    enum StudentStatus { ENROLLED, FINISHED; }
    composition Student -> Grades [*];
    association phonebook uni.Student [String] -> PhoneNumber;
  }
  association [0..1] Person (parent) <-> (child) de.monticore.life.Person [*];
}
```

The example shows a section of the [CD4ALanguageTeaser][LanguageTeaser]:
- Definition of two classes `Person` and `Student`
- `Person` is an abstract class
- `Student` extends `Person` (like in Java); interfaces would also be possible.
- Classes contain attributes, which have a type and a name
- Available types are basic types (from Java), imported types (like `Date`),
  and predefined forms of generic types (like `List`).
- Associations and compositions are defined between two classes,
  can have a name, a navigation information (e.g. `<->`), role names on both sides,
  multiplicities (like `[0..1]`) and certain predefined tags/stereotypes 
  (like `{ordered}`).
- Both, association and compositions can be qualified, for example by `[String]`.

Further examples can be found [here][ExampleModels].

## Available handwritten Extensions

### AST
- [`ASTCDDefinition`][ASTCDDefinition]
  adds methods for easy access to `CDType`s in
- [`ASTCDAssociation`][ASTCDAssociation]
  adds a method to retreive the name of the association

## Parser
- ([`CD4AnalysisParser`][CD4AParser])
  is extended to have additional transformations after parsing.
- The [`CD4AnalysisAfterParseTrafo`][CD4AAfterParseTrafo] and 
  [`CD4AnalysisAfterParseDelegatorVisitor`][CD4AAfterParseDelegatorVisitor]
  handle the transformation which need to be done after parsing.

## Symboltable
- [`CD4AnalysisSymbolTableCreatorDelegator`][CD4ASTCD]
  handles the creation and linking of the symbols of all the elements in CD4A
  and its sublanguages.
- The reference to a type (e.g. the type of an attribute) is stored in a 
  [`SymTypeExpression`][SymTypeExpression].
- De-/Serialization functionality for the symbol table uses the
  [`CD4AnalysisScopeDeSer`][CD4ASD] and for specific logic for serialization in
  [`CD4AnalysisSymbolTablePrinter`][CD4ASTP]
- CD4A contains TypesCalculator ([`DeriveSymTypeOfCD4Analysis`][CD4ATC]) for
  all its subgrammars.
- [`SymAssociation`][SymAssociation] is a class which is included in the
  symbol table and contains all information of a `CDAssociation`. It links to
  a `CDAssociationSymbol` (which only exists, when the association has a name),
  and to the two sides of the association which are stored in
  [`CDRoleSymbol`][CDRoleSymbol].

## Symbol kinds used by the CD4A language (importable or subclassed):
- CD4A uses [`OOSymbols`][OOSymbols] as the basis for the definition of
  its type-defining symbols.
- `OOTypeSymbol`s are used for all type-defining Symbols which implement
  `CDType` (`CDClass`, `CDInterface`, `CDEnum`)
- `FieldSymbol`s are implemented for `CDAttribute`, `CDEnumConstant`, and
  `CDRole`, defined in CD4Code: `CDParameter`
- `MethodSymbol`s are not used, because CD4A doesn't include methods, those
  are defined in CD4Code: `CDMethodSignature`

## Symbol kinds defined the CD4A language (exported):
- CD4A defines these symbols: `CDTypeSymbol`, `CDAssociationSymbol`, and
  `CDRoleSymbol`
- The other types either implement `CDTypeSymbol` or one of the `TypeSymbol`s
  and have no additional functionality or attributes

## Symbols imported by CD4A models:
- CD4A imports only Symbols of class diagrams

## Symbols exported by CD4A models:
- From the symbol introduced by CD4A, the following symbols are
  also exported:
  - `CDType`, the interface for all type definitions in the CD languages
  - `CDAssociation`, containing information about association and composition
  - `CDRole`, containing a side of an association
  - `CDMethodSignature`, containing the MethodSymbol attributes and a list of
    exceptions
- Furthermore CD4A models export symbols with kinds defined by
  other languages:
  - OOType, Field, Method
- CD4A has additional information in the SymbolTable:
  - `SymAssociation`, containing all general information of an association,
    because, when the association has no name, then there is no 
    `CDAssociationSymbol`

## Functionality: CoCos
- [`CD4ACoCosDelegator`][CD4ACoCos] combines all CoCos for all its sublanguages
- the individual CoCos can be found in the [`cocos`][cocos]-directory of each
  language
- the CoCos are separated in these categories:
  - `ebnf`, handles cases which should not be handled by the grammar itself,
    e.g., the name of an attribute is lowercase
  - `mcg`, contains CoCos that check semantics of the models, e.g., the list
    of valid `UMLModifier`s on an object
- CoCos ensure semantic correctness, here is a list of some of the important
  ones:
  - Uniqueness of names of e.g. classes in each package, attributes in each
    class
  - Classes hierarchy is free of cycles
  - Correct counter part on `extends` and `implements` keywords
  - Correct association qualifiers

### Types
- Currently: The BuiltinTypes can be found here [`BuiltInTypes`][BuiltInTypes].
- CD4A imports their types from foreign artifacts, respectively their
  provided symbol tables.
- CD4A provides a set of predefined types to be given 
  through grammar inclusion of [`MCCollectionTypes`][MCCollectionTypes]:
  - Primitives:  `char`, `int`, `double`, `float`, `long`, `boolean`, `short`, `byte`, `void`
  - ObjectTypes: `Character`, `Integer`, `Double`, `Float`, `Long`, `Boolean`,
    `Short`, `Byte`, `Void`, `Number`, `String`
  - UtilTypes: `List<T>`, `Optional<T>`, `Set<T>`, `Map<T1,T2>`
  - Special types, such as `Date` must then be provided through the imports of 
    other artifacts.
- The BuiltInTypes are not added automatically. They have to be added to the
  `GlobalScope`, by calling `addBuiltInTypes`. This enables more detailed
  control concerning the types that should be available.

### PrettyPrinter
- [`CD4AnalysisPrettyPrinter`][PrettyPrinter] contains a basic pretty printer
  for CD4A
- Externally available [PlantUML](https://plantuml.com/en/class-diagram)
  printer can be used to upload the model to plant uml and receive a graphical
  representation of the class diagram.

### Helper
There exist helper classes and methods which provide easier usage of often 
used functionality, e.g.:
- Different helpers for the symbol tables in [`cd/_symboltable`][STHelper]

### Reporting
- [`reporting`][reporting] provides an infrastructure for a complete reporting 
  functionality. This is used by MontiCore to provide additional information 
  concerning changes that have been applied to an AST. 
  
### CLI stand alone application:
- [`CDCLI`][CDCLI] contains a standalone, but extensible cli application which:
  1. Parses the given model
  2. Creates a symbol table
  3. Checks the CoCos

# CD4Code
CD4Code is a conservative extension of CD4Analysis and adds methods,
 constructors. Its main purpose is the usage in code generation.
* Conservative means: 
  * all models of CD4A also parse as CD4Code models
  * all functionalities developed for CD4A (and obeying the guidelines for
    extensibility) also apply for CD4Code models (if needed in extended form)

The grammar file is [`de.monticore.CD4Code`][CD4CGrammar].
 
## Example (in addition to CD4A)
```
classdiagram MyLife2 { 
  abstract class Person {
    protected String name;
    public Person(String name, int id);
    Set<Person> getParents();
  }
  class Student extends Person {
    abstract public change(StudentStatus status);
    void addFriends(Person... friends) throws Exception;
  }
  enum StudentStatus {
    ENROLLED(1),
    FINISHED(2);
  }
}
```

The example shows a section of the [CD4CodeLanguageTeaser.cd][CD4CLanguageTeaser]:
- the basic structure is the same as `CD4Analysis`
- class `Person` also contains methods and a constructor 
- methods and constructor can contain any number of arguments, separated by
  `,`
- there is no method body provided.
- enum constants can contain attributes (which call the constructor of the
  enum)

Further examples can be found [here][CD4CExampleModels].

## Handwritten Extensions
- CD4Code contains some handwritten extension (similar to CD4Analysis) like:
  - [`CD4CodePrettyPrinterDelegator`][CD4CodePrinter]
  - [`CD4CodeSymbolTableCreatorDelegator`][CD4CodeSTC]

## Usage
- CD4Code  can play its role as intermediate language (especially it's AST)
  capturing the structural part of the classes to be generated.
  It captures classes, and method signatures and allows to add
  templates as hook points that contain method bodies. Examples are:  
  * [MontiCoreCLI](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java): 
    Grammar -> 
    [Grammar AST encoded in CD4Code](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-generator/src/main/java/de/monticore/MontiCoreScript.java#L411) ->
    [Decoration for custom behavior](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java) -> 
    [Java code](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java)
  * Statechart -> State pattern encoded in CD4Code 
  -> Decoration by monitoring methods -> Java code.
- This is on contrast to CD4A which allows us to capture data structures for 
  example from the requirements elicitation activities.

[CD4AGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/CD4Analysis.mc4
[CD4CGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/CD4Code.mc4
[CDBasisGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/CDBasis.mc4
[CDIAEGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/CDInterfaceAndEnum.mc4
[CDAssocGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/CDAssociation.mc4
[CD4CBasisGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/CD4CodeBasis.mc4

[ASTCDDefinition]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cdbasis/_ast/ASTCDDefinition.java
[CD4AAfterParseTrafo]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd4analysis/_parser/CD4AnalysisAfterParseTrafo.java
[CD4AAfterParseDelegatorVisitor]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd4analysis/_parser/CD4AnalysisAfterParseDelegatorVisitor.java
[CD4ASD]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd4analysis/_symboltable/CD4AnalysisScopeDeSer.java
[CD4ASTP]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd4analysis/_symboltable/CD4AnalysisSymbolTablePrinter.java
[CD4ATC]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd4analysis/typescalculator/DeriveSymTypeOfCD4Analysis.java
[CD4ASTCD]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd4analysis/_symboltable/CD4AnalysisSymbolTableCreatorDelegator.java
[SymTypeExpression]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/java/de/monticore/types/check/SymTypeExpression.java
[SymAssociation]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cdassociation/_symboltable/SymAssociation.java
[CDRoleSymbol]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cdassociation/_symboltable/CDRoleSymbol.java
[STHelper]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd/_symboltable/CDSymbolTableHelper.java
[reporting]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd4analysis/reporting
[CDCLI]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd/cli/CDCLI.java
[LanguageTeaser]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/test/resources/de/monticore/cd4analysis/parser/MyLife.cd
[ExampleModels]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/test/resources/de/monticore/cd4analysis
[ASTCDAssociation]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_ast/ASTCDAssociation.java
[PrettyPrinter]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd4analysis/prettyprint/CD4AnalysisPrettyPrinter.java
[ASTCDType]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd4analysis/_ast/ASTCDType.java
[CD4ASTC]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd4analysis/_symboltable/CD4AnalysisSymbolTableCreator.java
[BuiltInTypes]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/BuiltInTypes.java

[CD4ACoCos]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/java/de/monticore/cd4analysis/cocos/CD4AnalysisCoCosDelegator.java
[CD4AParser]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd4analysis/_parser/CD4AnalysisParser.java
[CD4CodeSTC]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd4code/_symboltable/CD4CodeSymbolTableCreator.java
[CD4CLanguageTeaser]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/test/resources/de/monticore/cd4code/parser/MyLife2.cd
[CD4CExampleModels]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/test/resources/de/monticore/cd4code
[CD4CodePrinter]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd4code/prettyprint/CD4CodePrettyPrinterDelegator.java

[OOSymbols]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/symbols/OOSymbols.mc4
[MCCollectionTypes]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCCollectionTypes.mc4
