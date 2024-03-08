# Syntax Highlighting

Our Language Server's highlighting feature provides a visual aid for
class diagram analysis in the CD4Analysis language. 

The Language Server employs a set of token classification rules, each implementing the `TokenClassificationRule` interface. 
These rules are responsible for identifying specific patterns in tokens and applying appropriate token classifications for highlighting.
The rules are designed to identify different types of NameTokens in class diagrams.

An overview of supported highlighting functionality can be found in section [Overview](rule_overview.md). 

The technical details for extending the highlighting are available in section [Extending the Rule Set](rule_developing.md).