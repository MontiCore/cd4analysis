/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.creators;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.visitor.IVisitor;
import java.util.*;

/** Create the initial target CD as a copy of the original */
public class CopyCreator extends AbstractDecorator<CopyCreator.Created>
    implements ICreator<CopyCreator.Created>, CDBasisVisitor2 {

  /**
   * Initialized the decorated CD with a deep-copy of the original CD. The Original->Decorated Map
   * will be created on the fly. Do NOT call this method explicitly, instead this class as a
   * decorator
   *
   * @param originalCD the initial, original CD which will be copied
   */
  @Override
  public void visit(ASTCDCompilationUnit originalCD) {
    var ret = getData();
    ret.originalToDecorated.clear();
    ret.original = originalCD;
    ret.decorated = originalCD.deepClone();

    var origStack = new StackCreator(ret.original).stack;
    var decStack = new StackCreator(ret.decorated).stack;

    if (origStack.size() != decStack.size())
      throw new IllegalArgumentException("Stack size mismatch");

    while (!origStack.isEmpty()) {
      ret.originalToDecorated.put(origStack.pop(), decStack.pop());
    }
  }

  public Created getData() {
    return decoratorData.createDataIfAbsent(ICreator.class, Created::new);
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
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

  public static class Created implements ICreator.ICreatedData {
    protected ASTCDCompilationUnit original;
    protected ASTCDCompilationUnit decorated;
    protected final Map<ASTNode, ASTNode> originalToDecorated = new HashMap<>();

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
