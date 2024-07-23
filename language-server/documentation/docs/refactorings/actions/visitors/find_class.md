# FindClass Visitor

The FindClass Visitor implements the interface `CDBasisVisitor2`.
This interface defines methods for visiting nodes in the abstract syntax tree (AST).
The purpose of the FindClassVisitor class is to find a specific `ASTCDType` node in the AST of a parent node. 
The FindClassVisitor has two fields: find and found. The find field stores the `ASTCDType node` that the visitor is looking for. 
The found field stores the `ASTCDType node` that matches the find node, if any.

The full implementation can be seen above:

```java

public class FindClassVisitor implements CDBasisVisitor2 {
  private final ASTCDType find;
  private ASTCDType found;
  public FindClassVisitor(ASTCDType find) {
    this.find = find;
  }

  public static ASTCDType findClass(ASTNode parentAst, ASTCDType find) {
    CD4AnalysisTraverser traverser = CD4AnalysisMill.inheritanceTraverser();
    FindClassVisitor findClassVisitor = new FindClassVisitor(find);
    traverser.add4CDBasis(findClassVisitor);
    parentAst.accept(traverser);

    return findClassVisitor.getFound();
  }

  @Override
  public void visit(ASTCDClass node) {
    if (node.deepEquals(find))
      found = node;
  }

  public ASTCDType getFound() {
    return found;
  }
}
```

Similar to the FindClass Visitor, the [DeleteClass](delete_class.md) Visitor is implemented.
