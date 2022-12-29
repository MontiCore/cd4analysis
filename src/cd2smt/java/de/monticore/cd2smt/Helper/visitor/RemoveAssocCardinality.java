/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.Helper.visitor;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardMult;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;

public class RemoveAssocCardinality implements CDAssociationVisitor2 {
  @Override
  public void visit(ASTCDAssociation node) {
    node.getLeft().setCDCardinality(new ASTCDCardMult());
    node.getRight().setCDCardinality(new ASTCDCardMult());
  }
}
