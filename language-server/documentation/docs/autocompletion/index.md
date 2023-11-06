# Overview

## Content
- What is the autocompletion feature?
- Terminology
- Implemented autocompletion features
- Files in the project that are related to the autocompletion feature

## What is the autocompletion feature?
Autocompletion within the context of a language server for a programmer refers to an advanced software feature designed to streamline and enhance the coding experience. This feature is integrated into an Integrated Development Environment (IDE) or code editor (in case of the cd4Analysis language server: Visual Studio Code) and assists programmers by automatically suggesting and completing code elements as they type. It operates by analyzing the context of the code being written and offering relevant suggestions for keywords, function names, variable names, method invocations, and other syntactical components.

When a programmer starts typing a code snippet, the language server's autocompletion functionality predicts the programmer's intent and presents a list of possible completions in a dropdown or pop-up menu.

Programmers can then choose from these suggestions, either by selecting the desired completion option or continuing to type to refine the suggestion. This feature significantly accelerates the coding process, reduces typing errors, and helps programmers discover and utilize the available functions and methods more effectively.

In essence, autocompletion in a language server acts as an intelligent and proactive assistant that assists programmers in writing code more efficiently, enhancing their productivity, and promoting the creation of high-quality software.

## Terminology
In the following subsectiions, we will explain th 

### What is a token?
- A token is a foundational unit of representation in digital communication and information encoding. It takes the form of either a coherent character sequence or a sequence of Bits.
- Within the context of the cd4Analysis language server's autocompletion feature, a token effectively represents a completion option, offering programmers a streamlined way to enhance their code.

### What is a token path?
- Embedded within the realm of data interpretation and token-based structures, a token path serves as a guiding framework, leading to a designated set of tokens that can be distinctly identified by following the path itself.

## Why do we use token paths to identify tokens?
- When matching the path (which is a token path) at the position of line and character the programmer is currently typing at to a selected tokenpath, we can make autocompletion suggestions based on the position of line and character the programmer is currently typing at.
- Also this methodology allows us to add further autocompletions to the already suggested options by code. (This will be covered further in the documentation files that refer to the single automcompletion features.)ken path) at the position of line and character the programmer is currently typing at to a selected tokenpath, we can make autocompletion suggestions based on the position of line and character the programmer is currently typing at.
- Also this methodology allows us to add further autocompletions to the already suggested options by code. (This will be covered further in the documentation files that refer to the single automcompletion features.)

## Implemented autocompletion features
In the following section, we will introduce the implemented autocompletion features with a short example.

- Autocompletion feature for class names in an association.

In the following example the autocompletion feature for class names in an association would suggest "Employee" and "Share" at position [xxx] as possible autocompletions.

```
classdiagram MyCompany {
  class Employee {
    int salary;
  }
  class Share {
    int value;
  }
  association shareholding [1] [xxx]

}
```
The same would also happen in the following situation:
```
classdiagram MyCompany {
  class Employee {
    int salary;
  }
  class Share {
    int value;
  }
  association shareholding [1] Employee (shareholder) -- (owns) [xxx]

}
```

- Autocompletion feature for cardinalities in associations

In the following example the autocompletion feature for cardinalities in an association would suggest "[*]", "[x..x]" (where the programmer has to replace the x's after selecting the automcompletion option) and other options at position [xxx].

```
classdiagram MyCompany {
  class Employee {
    int salary;
  }
  class Share {
    int value;
  }
  association shareholding [xxx]]

}
```

The same would also happen in the following situation:
```
classdiagram MyCompany {
  class Employee {
    int salary;
  }
  class Share {
    int value;
  }
  association shareholding [1] Employee (shareholder) -- (owns) Share [xxx]

}
```

- autocompletion feature for navigation arrows in associations

In the following example the autocompletion feature for navigation arrows in an association would suggest "->", "--" and "<-" at position [xxx].

```
classdiagram MyCompany {
  class Employee {
    int salary;
  }
  class Share {
    int value;
  }
  association shareholding [1] Employee (shareholder) [xxx]

}
```

## Files in the project that are related to the autocompletion feature
In the following section, we explain what files are reliable for which autocompletion features, where to find them and link to the file specific documentation.

### CD4AnalysisCompletionProvider
CD4AnalysisCompletionProvider is a Java class that makes use of the top mechanism by extending CD4AnalysisCompletionProviderTOP.

The class consists solely out of a constructor with the signature: public CD4AnalysisCompletionProvider(CommonLanguageServer languageServer, DocumentManager documentManager, CD4AnalysisLanguageAccess languageAccess, ISymbolUsageResolutionProvider symbolUsageResolutionProvider) .

After developing a new autocompletion feature, the developer must register his autocompletion strategy in the CD4AnalysisCompletionProvider. The developer can do so by adding a new line of code to CD4AnalysisCompletionProvider's constructor that registers the new autocompletion strategy. The developer has to call "completionStrategyManager.registerCompletionStrategy(new [xxx](symbolUsageResolutionProvider, documentManager));", where [xxx] refers to the class name of the newly created autocompletion strategy class.

Example usage: completionStrategyManager.registerCompletionStrategy(new CD4AnalysisAssociationCompletionStrategy(symbolUsageResolutionProvider, documentManager));

File path: cd4analysis-language-server/src/main/java/de/monticore/cd4analysis/_lsp/features/completion/CD4AnalysisCompletionProvider.java

### CompletionStrategy
In order to understand for readers of this documentation to understand how our implemented completion strategies work, we need to explain the CompletionStrategy class, since all of our completion strategy classes implement CompletionStrategy and thus use it as a foundational base.

The CompletionStrategy class contains five methods. Three of the five methods are instance methods with defined behaviour. These can still be overwritten by classes implementing CompletionStrategy but we will explain in what situations doing so would be beneficial.

#### getImportedSymbols

getImportedSymbols takes an ExpectedToken object and and DocumentInformation object as parameters and returns an an empty new ArrayList.

```
default List<? extends ISymbol> getImportedSymbols(ExpectedToken token, DocumentInformation documentInformation){
    return new ArrayList<>();
   }
```

#### modifyParserCompletion

modifyParserCompletion takes an ExpectedToken object as a parameter and returns a List object containing the token's name.

```
default List<String> modifyParserCompletion(ExpectedToken token){
    return Collections.singletonList(token.tokenName);
}
```

#### getAdditionalCompletions

getAdditionalCompletions takes an ExpectedToken object, a DocumentInformation object and a String object as parameters and returns an empty new ArrayList.

- Can be overwritten while testing a new completion strategy to get testing results while testing the language server in Visual Studio Code. Also, it can be overridden to add additional tokens as completion options, if these are not part of the language server yet. We did so when implementing the CD4AnalysisAssociationCardinalityCompletionStrategy class.

```
default List<CompletionItem> getAdditionalCompletions(ExpectedToken token, DocumentInformation documentInformation, String contentUntilCompletion){
    return new ArrayList<>();
}
```

#### getSymbols

getSymbols takes an ExpectedToken object and a DocumentInformation object as parameters and it's method body has to be implemented by the class that implements the CompletionStrategy interface. We did not need getSymbols but had to add it as an instance method in the classes that implements the CompletionStrategy class. We let it return an empty Arraylist in all our autocompletion strategies.

```
List<? extends ISymbol> getSymbols(ExpectedToken token, DocumentInformation documentInformation);
```

#### matches
matches takes an ExpectedToken object as a parameter and returns a boolean. In our strategies we implemented matches in such a way that, the returned boolen is true, if the token path of the expected token matches the token path we are looking for. In such case, the possible autocompletion options are returned automatically united with the tokens added my getAdditionalCompletions. Otherwise the returned boolean will be false.

```
boolean matches(ExpectedToken expectedToken);
```

### CD4AnalysisAssociationCompletionStrategy

- Autocompletion feature for class names in an association. 

CD4AnalysisAssociationCompletionStrategy is a Java class that implements CompletionStrategy.

#### class members
TOKEN_PATH_REGEX is a regular expression that defines where we want to show completion options. We use TOKEN_PATH_REGEX in the matches method to match the correct token paths. Since the token path does not and should not change during executement of the language server, we made it a private variable.

documentManager is a reference to the DocumentManager. This variable should be final and is set when the constructor of the class that implements CompletionStrategy is called.

```
private final String TOKEN_PATH_REGEX = ".*cDAssociation\\.cDAssoc(.*)Side\\.mCQualifiedType\\.mCQualifiedName";
private final DocumentManager documentManager;
```

#### getSymbols

Since getSymbols returns tokens as completion options and often contains redundant tokens (depending on the completion strategy's use case), we had to filter the list prior to returning it. Thus we filter out redundant completions and solely return relevant completions.

In the CD4AnalysisAssociationCompletionStrategy.java file, we return only tokens that are instances of CDTypeSymbol and where isIsClass() returns true.

```
@Override
public List<? extends ISymbol> getSymbols(ExpectedToken token, DocumentInformation documentInformation) {
    List list = documentManager.getAllDocumentInformation(x -> x.uri.equals(documentInformation.uri))
            .stream()
            .map(x -> x.symbols)
            .flatMap(Collection::stream)
            .filter(x -> (x instanceof CDTypeSymbol) && (((CDTypeSymbol) x).isIsClass()))
            .collect(Collectors.toList());
    return list;
}
```

#### getAdditionalCompletions

We did not need any additional completions, thus we chose to not implement this method in our completion strategy.


#### matches
In this case we solely match for one token path regex. But depending on the completion strategy and the needed completion tokens a language server developer has to match for multiple token paths by using logical operators in the matches method.
```
@Override
public boolean matches(ExpectedToken expectedToken) {
    return expectedToken.tokenPathMatches(TOKEN_PATH_REGEX);
}
```

File path: cd4analysis-language-server/src/main/java/de/monticore/cd4analysis/_lsp/features/completion/strategy/CD4AnalysisAssociationCompletionStrategy.java

### CD4AnalysisAssociationCardinalityCompletionStrategy

- Autocompletion feature for cardinalities in associations.

CD4AnalysisAssociationCardinalityCompletionStrategy is a Java class that implements CompletionStrategy.

#### class members
TOKEN_PATH_LEFT_REGEX and TOKEN_PATH_RIGHT_REGEX are regular expressions that define where we want to show completion options. We use TOKEN_PATH_LEFT_REGEX and TOKEN_PATH_RIGHT_REGEX in the matches method to match the correct token paths. Since the token path does not and should not change during executement of the language server, we made it a private variable.

documentManager is a reference to the DocumentManager. This variable should be final and is set when the constructor of the class that implements CompletionStrategy is called.

For the Autocompletion feature for cardinalities in associations we needed two token paths since there are two places where the same completion options should be shown. Take a look at the example in the "implemented autocompletion features" section above to understand the two positions at which we need to match the token path

```
private final String TOKEN_PATH_LEFT_REGEX = "cDCompilationUnit.cDDefinition.cDElement.cDAssociation.cDAssocLeftSide";
private final String TOKEN_PATH_RIGHT_REGEX = "cDCompilationUnit.cDDefinition.cDElement.cDAssociation";

private final DocumentManager documentManager;
```

#### getSymbols

Since getSymbols returns tokens as completion options and often contains redundant tokens (depending on the completion strategy's use case), we had to filter the list prior to returning it. Thus we filter out redundant completions and solely return relevant completions.

In the CD4AnalysisAssociationCardinalityCompletionStrategy class, we returned an empty object of ArrayList since we did not needed getSymbols to filter the tokens. Cardinalities were missing in the DocumentInformation. Thus the method returned an empty ArrayList object. We added useful cardinalities in the getAdditionalCompletions method which will be explained next.

```
@Override
public List<? extends ISymbol> getSymbols(ExpectedToken token, DocumentInformation documentInformation) {
    return new ArrayList<>();
}
```

#### getAdditionalCompletions

We called the getAdditionalCompletions method of the upper class and saved them in a List that stores CompletionItems. We then made use of the List class and added new CompletionItems. Which will be shown as possible completions, when the matches method matches the token path. Now cardinalities arrows are possible completions.

```
@Override
    public List<CompletionItem> getAdditionalCompletions(ExpectedToken token, DocumentInformation documentInformation, String contentUntilCompletion) {
        List<CompletionItem> itemList = CompletionStrategy.super.getAdditionalCompletions(token, documentInformation, contentUntilCompletion);
    itemList.add(new CompletionItem("[]"));
    itemList.add(new CompletionItem("[*]"));
    itemList.add(new CompletionItem("[1..*]"));
    itemList.add(new CompletionItem("[ .. ]"));

    return itemList;
}
```

#### matches
In this case we match for two token path regular expressions and thus use the logical operator "||".

```
@Override
public boolean matches(ExpectedToken expectedToken) {
    return expectedToken.tokenPathMatches(TOKEN_PATH_LEFT_REGEX) || expectedToken.tokenPathMatches(TOKEN_PATH_RIGHT_REGEX);
}
```

File path: cd4analysis-language-server\src\main\java\de\monticore\cd4analysis\_lsp\features\completion\strategy\CD4AnalysisAssociationCardinalityCompletionStrategy.java















### CD4AnalysisAssociationCardinalityCompletionStrategy

- autocompletion feature for navigation arrows in associations

CD4AnalysisAssociationCardinalityCompletionStrategy is a Java class that implements CompletionStrategy.

#### class members
TOKEN_PATH_REGEX is a regular expression that define where we want to show completion options. We use TOKEN_PATH_REGEX in the matches method to match the correct token paths. Since the token path does not and should not change during executement of the language server, we made it a private variable.

documentManager is a reference to the DocumentManager. This variable should be final and is set when the constructor of the class that implements CompletionStrategy is called.

For the Autocompletion feature for navigation arrows we only need one token path regular expression.

```
private final String TOKEN_PATH_REGEX = "cDCompilationUnit.cDDefinition.cDElement.cDAssociation.cDAssocLeftSide.cDOrdered";
private final DocumentManager documentManager;
```

#### getSymbols

Since getSymbols returns tokens as completion options and often contains redundant tokens (depending on the completion strategy's use case), we had to filter the list prior to returning it. Thus we filter out redundant completions and solely return relevant completions.

In the CD4AnalysisAssociationCardinalityCompletionStrategy class, we returned an empty object of ArrayList since we did not needed getSymbols to filter the tokens. Navigation arrows were missing in the DocumentInformation. Thus the method returned an empty ArrayList object. We added useful navigation arrows in the getAdditionalCompletions method which will be explained next.

```
@Override
    public List<? extends ISymbol> getSymbols(ExpectedToken token, DocumentInformation documentInformation) {
        return new ArrayList<>();
    }
```

#### getAdditionalCompletions

We called the getAdditionalCompletions method of the upper class and saved them in a List that stores CompletionItems. We then made use of the List class and added new CompletionItems. Which will be shown as possible completions, when the matches method matches the token path. Now navigation arrows are possible completions.

```
public List<CompletionItem> getAdditionalCompletions(ExpectedToken token, DocumentInformation documentInformation, String contentUntilCompletion) {
        List<CompletionItem> itemList = CompletionStrategy.super.getAdditionalCompletions(token, documentInformation, contentUntilCompletion);
        itemList.add(new CompletionItem("->"));
        itemList.add(new CompletionItem("<-"));
        itemList.add(new CompletionItem("--"));
        return itemList;
    }
```

#### matches
In this case, we match for one token path regular expressions.

```
@Override
    public boolean matches(ExpectedToken expectedToken) {
            return expectedToken.tokenPathMatches(TOKEN_PATH_REGEX);
    }
```

File path: cd4analysis-language-server\src\main\java\de\monticore\cd4analysis\_lsp\features\completion\strategy\CD4AnalysisAssociationNavigationCompletionStrategy.java