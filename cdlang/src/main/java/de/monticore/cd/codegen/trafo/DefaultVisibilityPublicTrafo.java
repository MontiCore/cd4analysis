/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.trafo;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._visitor.UMLModifierVisitor2;

public class DefaultVisibilityPublicTrafo implements UMLModifierVisitor2, CDBasisVisitor2,
    CD4CodeBasisVisitor2 {

  protected int classDepth;

  @Override
  public void visit(ASTCDClass node) {
    classDepth++;
  }

  @Override
  public void endVisit(ASTCDClass node) {
    classDepth--;
  }

  @Override
  public void visit(ASTCDAttribute node) {
    relaxClassMemberVisibility(node.getModifier());
  }

  @Override
  public void visit(ASTCDMethod node) {
    relaxClassMemberVisibility(node.getModifier());
  }

  @Override
  public void visit(ASTCDConstructor node) {
    relaxClassMemberVisibility(node.getModifier());
  }

  protected void relaxClassMemberVisibility(ASTModifier modifier) {
    if (classDepth > 0 && !modifier.isPublic() && !modifier.isProtected()) {
      modifier.setPrivate(false);
      modifier.setProtected(true);
    }
  }
  
  @Override
  public void visit(ASTModifier node) {
    if (!node.isPublic() && !node.isPrivate() && !node.isProtected()) {
      node.setPublic(true);
    }
  }
  
}
