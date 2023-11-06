# DeleteClass Visitor
The VisitorPattern is also used while deleting classes from the AST.

When entering a `ASTCDPackage node` or `ASTCDDefinition definition` the respective element is removed from the element list.
Both types need to be handled, as the class might be contained in a sub-package or not. 

```java
public class DeleteClassVisitor implements CDBasisVisitor2 {
  private final ASTCDType toDelete;

  // Similar to FindClass Visitor (1)

  @Override
  public void visit(ASTCDPackage node) {
    node.getCDElementList().removeIf(element -> element.deepEquals(toDelete));
  }

  @Override
  public void visit(ASTCDDefinition definition) {
    definition.getCDElementList().removeIf(element -> element.deepEquals(toDelete));
  }

}


```

1. For more information, refer to the [FindClass](find_class.md) Visitor description.