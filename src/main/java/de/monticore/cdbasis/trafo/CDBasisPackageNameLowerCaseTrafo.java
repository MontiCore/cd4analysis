/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.trafo;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

public class CDBasisPackageNameLowerCaseTrafo implements CDBasisVisitor2 {

  @Override
  public void visit(ASTCDPackage node) {
    ASTMCQualifiedName qName = node.getMCQualifiedName();
    for (int i = 0; i < qName.sizeParts(); i++) {
      qName.setParts(i, qName.getParts(i).toLowerCase());
    }
  }

  public void transform(ASTCDCompilationUnit ast) {
    CDBasisTraverser t = CDBasisMill.traverser();
    t.add4CDBasis(this);
    ast.accept(t);
  }
}
