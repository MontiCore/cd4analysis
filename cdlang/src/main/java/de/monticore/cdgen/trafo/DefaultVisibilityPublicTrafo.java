/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.trafo;

import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._visitor.UMLModifierVisitor2;

public class DefaultVisibilityPublicTrafo implements UMLModifierVisitor2 {
  @Override
  public void visit(ASTModifier node) {
     if (!node.isPublic() && !node.isPrivate() && !node.isProtected()) {
       node.setPublic(true);
     }
  }

}
