/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.creators;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.visitor.IVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Create the initial target CD as a copy of the original
 */
public class CopyCreator {


  /**
   * Initialized the decorated CD with a deep-copy of the original CD.
   * The Original->Decorated Map will be created on the fly
   *
   * @param originalCD the initial, original CD which will be copied
   * @return the data
   */
  public Created createFrom(ASTCDCompilationUnit originalCD) {
    var ret = new Created(originalCD);
    ret.decorated = originalCD.deepClone();

    var origStack = new StackCreator(ret.original).stack;
    var decStack = new StackCreator(ret.decorated).stack;

    if (origStack.size() != decStack.size())
      throw new IllegalArgumentException("Stack size mismatch");

    while (!origStack.isEmpty()) {
      ret.originalToDecorated.put(origStack.pop(), decStack.pop());
    }

    return ret;
  }

  static class StackCreator implements IVisitor {
    final Stack<ASTNode> stack = new Stack<>();

    @Override
    public void visit(ASTNode node) {
      stack.push(node);
    }

    public StackCreator(ASTNode root) {
      var t = CD4CodeMill.inheritanceTraverser();
      t.add4IVisitor(this);
      root.accept(t);
    }
  }

  public static class Created {
    protected final ASTCDCompilationUnit original;
    protected ASTCDCompilationUnit decorated;
    protected final Map<ASTNode, ASTNode> originalToDecorated = new HashMap<>();

    public Created(ASTCDCompilationUnit original) {
      this.original = original;
    }

    public ASTCDCompilationUnit getOriginal() {
      return original;
    }

    public ASTCDCompilationUnit getDecorated() {
      return decorated;
    }

    public Map<ASTNode, ASTNode> getOriginalToDecoratedMap() {
      return originalToDecorated;
    }
  }
}
