/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.trafo;


import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.types.check.SymTypeExpressionFactory;

public class CDBasisDefaultSuperTypeTrafo implements CDBasisVisitor2 {

  @Override
  public void visit(ASTCDClass node) {
    if (node.getSuperclassList().isEmpty()) {
      node.getEnclosingScope().resolveType("Object")
        .ifPresent((t) -> node.getSymbol().addSuperTypes(SymTypeExpressionFactory.createTypeObject(t)));
    }
  }
}
