<!-- (c) https://github.com/MontiCore/monticore -->
# UML/P CD
The language for UML class diagrams is split up into 2 languages:
- **CD4Analysis**: basic cd language with classes, attributes, associations 
- **CD4Code**: extension with methods, constructors, values

# CD4Analysis
The main pupose of this language is the modeling of data structure, which 
typically emerges as result of requirements elicitation activities.

The main grammar file is [`de.monticore.cd.CD4Analysis`][CD4AGrammar].

## Example
```
classdiagram MyLife { 
  abstract class Person {
    int age;
    Date birthday;
    List<String> nickNames;
  }
  class Student extends Person {
    StudentStatus status;
  }
  enum StudentStatus { ENROLLED, FINISHED; }
  
  composition Person -> Address [*]  {ordered};
  association [0..2] Person (parent) <-> (child) Person [*];
  association phonebook Person [String] -> TelefoneNumber ;
}
```

The example shows a section of the [CD4ALanguageTeaser.cd][LanguageTeaser]:
- Definition of two classes `Person` and `Student`
- `Person` is an abstract class
- `Student` extends from `Person` (like in Java); interfaces would also be possible.
- Classes contain attributes, which have a type and a name
- Available types are basic types (from Java), imported types (like `Date`),
  and predefined forms of generic types (like `List`).
- Associations and compositions are defined between two classes,
  can have a name,  a navigation information (e.g. `<->`), role names on both sides,
  multiplicities (like `[0..1]`) and certain predefined tags/stereotypes 
  (like `{ordered}`).
- Both, association and compositions can be qualified, for example by `[String]`.

Further examples can be found in [here][ExampleModels].

## Available handwritten Extensions

### AST
- [`de.monticore.cd.cd4analysis._ast.ASTCDAssociation`][ASTCDAssociation]
  defines several symbols, i.e. for the left and right roles.
- [`de.monticore.cd.cd4analysis._ast.ASTCDType`][ASTCDType]
  adds methods for easy access to `CDType`s in 

## Parser
- ([`de.monticore.cd.cd4analysis._parser.CD4AnalysisParser`][CD4AParser])
  is extended to have additional checks like the classdiagram's name
  has to match the file name

## Symboltable
- De-/Serialization functionality for the symbol table 
  ([`de.monticore.cd.cd4analysis._symboltable.serialization`][serialization])
- [`de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreator`][CD4ASTC]
  handles the creation and linking of the symbols
- [`de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol`][CDAssocSymbol]
  contains al relevant information of the association (including links to
  role symbols, etc.)

## Functionality: CoCos

-  [`de.monticore.cd.CD4ACoCos`][CD4ACoCos] combines all CoCo's
-  the individual CoCos can be found in 
   [`de.monticore.cd.cocos`][cocos]
- CoCos ensure semantic correctness, here is a list of some of the important ones:
  - Uniqueness of names of e.g. classes, attributes (in each class)
  - Cycleless extensions of classes
  - Correct counter part on `extends` and `implements` keywords
  - Correct association qualifiers
  - Coding conventions like correct cased class and attribute names
  - Check for correctness of a modifier on a given element

### Functionality: Builtin/Predefined Types
The BuiltinTypes can be found here
 [`de.monticore.cd.BuiltInTypes`][BuiltInTypes].
 
CD4Analysis comes with already predefined types to be used in the class
 diagrams. These consist of the basic Java types like `String`, `int`, `double`
 , but also contain `Date` and the "simple generic" types `List`, `Optional`,
  `Set`, and `Map`.

### PrettyPrinter
- The basic pretty printer for CD4A is [`de.monticore.cd.prettyprint.CDPrettyPrinter`][PrettyPrinter]
- An additional [PlantUML](https://plantuml.com/en/class-diagram) printer can
 be used to upload the model to plant uml and receive a graphical representation of the class diagram.

### Helper
There exist different helper classes and methods which provide easier usage of
 often used functionality:
- The transformations in 
[`de.monticore.cd.transformation`][transformation] provide functionality to
 easily create and add elements to specific ast nodes
- The 
[`de.monticore.cd.prettyprint.AstPrinter`][ASTPrinter] provides helper
 functionality for pretty printing

### Reporting
[`de.monticore.cd.reporting`][reporting] provides an infrastructure for a
 complete reporting functionality. This is used MontiCore to provide additional
 information what changes have been done on the AST. 
  
### CLI Application
[`de.monticore.cd.CD4ACLI`][CD4ACLI] contains a standalone cli application
 which:
1. Parses the given model
2. Creates a symbol table
3. Checks the CoCos

# CD4Code
CD4Code is a conservative extension of CD4Analysis and adds methods,
 constructors, and default values for attributes. Its main purpose is the usage
 in code generation.

The grammar file is
 [`de.monticore.cd.CD4Code`][CD4CodeGrammar].
 
## Example
```
abstract class Person {
  protected String name;
  public Person(String name);
  public String getName();
}

class Student extends Person {
  public Student(String name, StudentStatus status);
  StudentStatus status;
}
```

The example shows a section of the [CD4CodeLanguageTeaser.cd][CD4CodeLanguageTeaser]:
- the basic structure is the same as `CD4Analysis`
- class `Person` contains a constructor with an argument for the name and a
  getter
- methods and constructor can contain any number of arguments, separated by
  `,`

Further examples can be found in [here][CD4CodeExampleModels].

## Handwritten Extensions
- There are no handwritten extensions except the functionality for the
 [`de.monticore.cd.cd4code.CD4CodePrettyPrinterDelegator`][CD4CodePrinter]

[CD4AGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Analysis.mc4
[LanguageTeaser]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/test/resources/de/monticore/umlcd4a/parser/CD4ALanguageTeaser.cd
[ExampleModels]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/test/resources/de/monticore/umlcd4a
[ASTCDAssociation]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_ast/ASTCDAssociation.java
[ASTCDType]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_ast/ASTCDType.java
[CD4AParser]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_parser/CD4AnalysisParser.java
[serialization]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd/cd4analysis/_symboltable/serialization
[CD4ASTC]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_symboltable/CD4AnalysisSymbolTableCreator.java
[CDAssocSymbol]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_symboltable/CDAssociationSymbol.java
[cocos]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd/cocos
[CD4ACoCos]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/CD4ACoCos.java
[BuiltInTypes]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/BuiltInTypes.java
[PrettyPrinter]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/prettyprint/CDPrettyPrinter.java
[transformation]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd/transformation
[ASTPrinter]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/prettyprint/AstPrinter.java
[reporting]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd/reporting
[CD4ACLI]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/CD4ACLI.java
[CD4CodeGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Code.mc4
[CD4CodeLanguageTeaser]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/test/resources/de/monticore/cd4code/CD4CodeLanguageTeaser.cd
[CD4CodeExampleModels]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/test/resources/de/monticore/cd4code
[CD4CodePrinter]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4code/CD4CodePrettyPrinterDelegator.java
