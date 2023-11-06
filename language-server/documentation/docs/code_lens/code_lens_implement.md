# Overview

Generally the Code Lenses are implemented in a similar way to the [Code Actions](../refactorings/actions/code_action_extension.md) used for the [Refactorings](../refactorings/index.md).

## Registering CodeLenses

The `CD4AnalysisCodeLensProvider` class provides Code Lenses for the CD4Analysis language
and extends the `CodeLensProvider` from MontiCore. It adds Code Lenses for the CD4Analysis language.
These Code Lenses are implemented using different strategies for the `CodeLensStrategy` interface.

The constructor of the `CD4AnalysisCodeLensProvider` takes two parameters: a `DocumentManager` and an `AstPrettyPrinter<ASTCDCompilationUnit>`.
The `DocumentManager` is an object that keeps track of the documents opened in the editor and their corresponding ASTs.
The ASTs are abstract syntax trees representing the structure and semantics of the CD4Analysis models.
The `AstPrettyPrinter<ASTCDCompilationUnit>` is an object that can pretty print an AST back to a CD4Analysis model as a string.

The constructor also creates an empty list of `CodeLensStrategy` objects, which are used to store the different kinds of Code Lenses.

### The `codeLens` Method 

The main method of this class is the `codeLens` method.
This method is called by the LSP server when a Code Lens request is received from the editor.
The method takes one parameter of the type `TextDocumentItem`.
It represents the document on which the Code Lens is requested.

The method returns a list of `CodeLens` objects, representing the possible Code Lenses that can be applied to the document.

The method first calls the `codeLens` method from the superclass.
Then, it iterates over the list of `CodeLensStrategy` objects and applies each strategy to the document, context, and range.
Each strategy returns a list of Code Lenses specific to the CD4Analysis language and relevant for the given situation.
The method adds all these Code Lenses to the list returned by the superclass and returns the final list.

### CodeLensStrategy

The `CodeLensStrategy` interface defines two methods: `matches` and `apply`.
The `matched` method takes one parameter of the type `MatchedToken` and returns a boolean. It is used to check if the Code Lens is supposed to be applied to the given token or not.
The `apply` method takes one parameter of the type `MatchedToken` and returns a list of Code Lenses.
Each subclass of this interface implements a specific kind of Code Lens for the CD4Analysis language.

```java
public interface CodeLensStrategy {
    boolean matches(MatchedToken matchedToken);

    Optional<? extends CodeLens> apply(MatchedToken matchedToken);
}
```

## Developing New Code Lenses

### Working with the AST

If a `CodeLensStrategy`'s `apply` method is being called, the LSP is checking the given document.
Based on the `MatchedToken` parameter given to the method, it can evaluate meta data of the document and
represent it as a CodeLens in the editor.
One example is the implemented Code Lens for showing the associations between defined classes.

![](../assets/images/code_lens_assocs.png)

Over each AST node with the token path matching the regex `.*.cDType.cDClass` the total number of calculated associations is rendered above the class definition.
The regex is checked in the `matches` method.