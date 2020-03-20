# UML/P CD
The language for UML class diagrams is split up into 2 languages:
- **CD4Analysis**: basic cd language with classes, attributes, associations 
- **CD4Code**: extension with methods, constructors, values

# CD4Analysis
The main pupose of this language is the modeling of data structure.

The grammar file is [`de.monticore.cd.CD4Analysis`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Analysis.mc4).

## Handwritten Extensions
### AST
- Additional symbols for the left and right roles are added to [`de.monticore.cd.cd4analysis._ast.ASTCDAssociation`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_ast/ASTCDAssociation.java)
- Methods which provide easier access to `CDType`s in [`de.monticore.cd.cd4analysis._ast.ASTCDType`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_ast/ASTCDType.java)
## Parser
- The parser is extended to have additional checks like the classdiagram's name has to match the file name ([`de.monticore.cd.cd4analysis._parser.CD4AnalysisParser`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_parser/CD4AnalysisParser.java))
## Symboltable
- De-/Serialization functionality for the symbol table ([`de.monticore.cd.cd4analysis._symboltable.serialization`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd/cd4analysis/_symboltable/serialization))
- The [`de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreator`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_symboltable/CD4AnalysisSymbolTableCreator.java) handles the linking of the symbols and creates new ones depending on the given nodes
- The [`de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4analysis/_symboltable/CDAssociationSymbol.java) contains a lot of additional information of the association

## Functionality
### CoCos
The CoCos can be found in [`de.monticore.cd.cocos`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd/cocos) and are combined accessible in [`de.monticore.cd.CD4ACoCos`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/CD4ACoCos.java).

The context conditions check different parts of the models, to ensure the semantic correctness, here is a list of some of the important ones:
- Uniqueness of names of e.g. classes, attributes (in each class)
- Cycleless extensions of classes
- Correct counter part on `extends` and `implements` keywords
- Correct association qualifiers
- Coding conventions like correct cased class and attribute names
- Check for correctness of a modifier on a given element

### Builtin/Predefined Types
The BuiltinTypes can be found here [`de.monticore.cd.BuiltInTypes`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/BuiltInTypes.java).

CD4Analysis comes with already predefined types to be used in the class diagrams. These consist of the basic Java types like `String`, `int`, `double`, but also contain `Date` and the "simple generic" types `List`, `Optional`, `Set`, and `Map`.

### PrettyPrinter
- The basic pretty printer for CD4A is [`de.monticore.cd.prettyprint.CDPrettyPrinter`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/prettyprint/CDPrettyPrinter.java)
- An additional [PlantUML](https://plantuml.com/en/class-diagram) printer can be used to upload the model to plant uml and receive a graphical representation of the class diagram.

### Helper
There exist different helper classes and methods which provide easier usage of often used functionality:
- The transformations in [`de.monticore.cd.transformation`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd/transformation) provide functionality to easily create and add elements to specific ast nodes
- The [`de.monticore.cd.prettyprint.AstPrinter`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/prettyprint/AstPrinter.java) provides helper functionality for pretty printing

### Reporting
[`de.monticore.cd.reporting`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/tree/develop/src/main/java/de/monticore/cd/reporting) provides an infrastructure for a complete reporting functionality. This is used MontiCore to provide additional information what changes have been done on the AST. 
  
### CLI Application
[`de.monticore.cd.CD4ACLI`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/CD4ACLI.java) contains a standalone cli application which:
1. Parses the given model
2. Creates a symbol table
3. Checks the CoCos

# CD4Code
CD4Code is a conservative extension of CD4Analysis and adds methods, constructors, and default values for attributes. Its main purpose is the usage in code generation.

The grammar file is [`de.monticore.cd.CD4Code`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Code.mc4).

## Handwritten Extensions
- There are no handwritten extensions except the functionality for the [`de.monticore.cd.cd4code.CD4CodePrettyPrinterDelegator`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/java/de/monticore/cd/cd4code/CD4CodePrettyPrinterDelegator.java)