# Extract Superclass
The Extract Superclass Refactoring is a technique that creates a new superclass from existing classes and moves equal attributes to the super class.
This way, code duplication is reduced by inheritance. 
The Extract Superclass Refactoring is also useful when a class has too many responsibilities or features, and it should be split up.

![](../../assets/images/extract_superclass_action.png)


### Details
**Execution**: Fully mark all classes that should be used to extract the new superclass.

The new parent class is always named "SuperClass", therefore, the name must be available.