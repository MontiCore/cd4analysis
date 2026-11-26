/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.trafo;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.umlmodifier._ast.ASTModifier;

/**
 * Propagates the visibility of an association (e.g. {@code private association A -> B;}) to its
 * roles, unless a role declares its own visibility.
 * <p>
 * Must be applied before {@link DefaultVisibilityPublicTrafo}, as that trafo would otherwise mark
 * the roles as public. When both are added to the same traverser, this happens automatically, as
 * the association is visited before the modifiers of its roles.
 */
public class AssociationVisibilityToRolesTrafo implements CDAssociationVisitor2 {
  
  @Override
  public void visit(ASTCDAssociation node) {
    ASTModifier assocModifier = node.getModifier();
    propagate(assocModifier, node.getLeft());
    propagate(assocModifier, node.getRight());
  }
  
  protected void propagate(ASTModifier assocModifier, ASTCDAssocSide side) {
    ASTModifier roleModifier = side.getModifier();
    if (roleModifier.isPublic() || roleModifier.isProtected() || roleModifier.isPrivate()) {
      return; // the role declares its own visibility
    }
    roleModifier.setPublic(assocModifier.isPublic());
    roleModifier.setProtected(assocModifier.isProtected());
    roleModifier.setPrivate(assocModifier.isPrivate());
  }
  
}
