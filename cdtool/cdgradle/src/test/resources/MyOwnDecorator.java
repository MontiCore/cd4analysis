/* (c) https://github.com/MontiCore/monticore */
package mc;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;

/**
 * Pseudo Decorator
 */
public class MyOwnDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {


  @Override
  public void visit(ASTCDClass node) {
    // Only act if we should decorate the class
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      System.out.println("I am decorating " + node.getName());
    } else {
      System.out.println("I am NOT decorating " + node.getName());
    }
  }


  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
