# Collapse Hierarchy

The Collapse Hierarchy refactoring is a technique that simplifies the inheritance structure of a class hierarchy by merging a child class into its parent class. 
This refactoring is useful when the child class does not add any new functionality or behavior to the parent class, and thus it is redundant and unnecessary. 
By collapsing the child class into the parent class, complexity is reduced.

An example of the changes after execution is shown below.

![](../../assets/images/collapse_hierarchy_action.png)

### Details
**Execution**: Select the class name of the class that you want to merge with its parent class. The selected class must inherit from another class.

The CollapseHierarchy strategy supports super classes that are in different files. 

If attributes from the super class are already present, a simple counter is added to the attribute name until the attribute is unique again.