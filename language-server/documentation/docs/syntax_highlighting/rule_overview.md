# Overview

The following SemanticTokens are detected using Regex Path Identifiers:

* Keywords
* Classes
* EnumMembers
* EnumNames
* Interfaces
* Packages

If you want to learn more about the implementation, please refer to the [Developer Guide](rule_developing.md).

### The result can be observed in the following comparison:
![](../assets/images/highlighting_comparison.png)

Different highlighting is applied to the example above. 
The class-diagram name and the other class names are now easier distinguishable.
Additionally, the whole Enum structure in line 11 is now easier readable,
as each different kind of token now has an own color.
