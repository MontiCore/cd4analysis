# Extending the Rule Set

This part of the documentation is intended for developers who want to extend the set of rules.

## Basic

1. Implement the `TokenClassificationRule` interface and override the `matches` and `apply` methods
2. In the `matches` method, create a Regex that describes best the path of the required NameToken
3. Assign a [Semantic Token](https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#textDocument_semanticTokens) in the `apply` method

## Example

```java title="Example for Highlighting Enum Members"
public class HighlightEnumMemberNameRule implements TokenClassificationRule {

  @Override
  public boolean matches(Token token) {
    return (token.tokenPathMatches(".*.cDElement.*.cDEnum.*cDEnumConstant")) // (1)
            && token.isNameToken();
  }

  @Override
  public Optional<TokenClassification> apply(Token token) {
    return Optional.of(
            new TokenClassification(SemanticTokenTypesWrapper.EnumMember)); // (2)
  }
}
```

1. The TokenPath can be found via VSCode with the LSP Debug feature
2. SemanticToken as defined in the [Language Server Protocol](https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#textDocument_semanticTokens)

### LSP Debug Feature
You can access the LSP debug feature in VSCode by following the steps below:

1. Open the VSCode Command Palette with CTRL+SHIFT+P
2. Execute: Open LSP Token Debug Window
3. Navigate to the tab Highlighting