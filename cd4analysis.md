# UML/P CD
The language for UML class diagrams is split up into 2 languages:
- **CD4Analysis**: basic cd language with classes, attributes, associations 
- **CD4Code**: extension with methods, constructors, values

# CD4Analysis
The main pupose of this language is the modeling of data structure.

The grammar file is [`de.monticore.cd.CD4Analysis`][CD4AGrammar].

## Handwritten Extensions
### AST
- Additional symbols for the left and right roles are added to
 [`de.monticore.cd.cd4analysis._ast.ASTCDAssociation`][ASTCDAssociation]
- Methods which provide easier access to `CDType`s in 
 [`de.monticore.cd.cd4analysis._ast.ASTCDType`][ASTCDType]
## Parser
- The parser is extended to have additional checks like the classdiagram's name
 has to match the file name
 ([`de.monticore.cd.cd4analysis._parser.CD4AnalysisParser`][CD4AParser])
## Symboltable
- De-/Serialization functionality for the symbol table 
([`de.monticore.cd.cd4analysis._symboltable.serialization`][serialization])
- The [`de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreator`][CD4ASTC]
 handles the linking of the symbols and creates new ones depending on the given nodes
- The [`de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol`][CDAssocSymbol]
 contains a lot of additional information of the association

## Functionality
### CoCos
The CoCos can be found in 
 [`de.monticore.cd.cocos`][cocos] and are combined accessible in
 [`de.monticore.cd.CD4ACoCos`][CD4ACoCos].

The context conditions check different parts of the models, to ensure the
 semantic correctness, here is a list of some of the important ones:
- Uniqueness of names of e.g. classes, attributes (in each class)
- Cycleless extensions of classes
- Correct counter part on `extends` and `implements` keywords
- Correct association qualifiers
- Coding conventions like correct cased class and attribute names
- Check for correctness of a modifier on a given element

### Builtin/Predefined Types
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

## Handwritten Extensions
- There are no handwritten extensions except the functionality for the
 [`de.monticore.cd.cd4code.CD4CodePrettyPrinterDelegator`][CD4CodePrinter]

[CD4AGrammar]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Analysis.mc4
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
[CD4CodePrinter]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4code/CD4CodePrettyPrinterDelegator.java
