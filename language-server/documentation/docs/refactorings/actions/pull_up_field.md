# Pull-Up Field

The Pull-Up Field refactoring is a technique to eliminate duplication of fields in subclasses by moving them to a superclass. 
This way, the subclasses can inherit the field from the superclass and avoid code duplication. 
The Pull-Up Field refactoring can improve the design of the class hierarchy.

![](../../assets/images/pull_up_field_action.png)

### Details
**Execution**: Select an attribute in a class that extends another class.

If an attribute with the same name already exists, a counter is added to the attribute where the refactoring was triggered.
The counter is incremented until the name is valid.