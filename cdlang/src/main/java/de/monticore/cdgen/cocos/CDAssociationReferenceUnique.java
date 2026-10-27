/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Checks that generated association references are unique.
 */
public class CDAssociationReferenceUnique extends CDAssociationUniqueInHierarchy {

  @Override
  public void check(ASTCDDefinition node) {
    List<ASTCDAssociation> alreadyChecked = new ArrayList<>();

    // we check for each pair of associations
    for (ASTCDAssociation assoc2 : node.getCDAssociationsList()) {
      for (ASTCDAssociation assoc1 : alreadyChecked) {
        for (AssociationReference ref1 : getAssociationReferences(assoc1)) {
          for (AssociationReference ref2 : getAssociationReferences(assoc2)) {
            if (ref1.name.equals(ref2.name)) {
              checkRef(node, ref1.sourceType, ref2.sourceType, assoc2);
            }
          }
        }
      }

      alreadyChecked.add(assoc2);
    }
  }

  @Override
  protected void checkRef(ASTCDDefinition node, ASTCDType type1, ASTCDType type2,
      ASTCDAssociation assoc1) {
    if (type1 == null || type2 == null) {
      return;
    }
    super.checkRef(node, type1, type2, assoc1);
  }

  protected List<AssociationReference> getAssociationReferences(ASTCDAssociation assoc) {
    List<AssociationReference> references = new ArrayList<>();

    boolean navigableLeft = assoc.getCDAssocDir().isDefinitiveNavigableLeft();
    boolean navigableRight = assoc.getCDAssocDir().isDefinitiveNavigableRight();
    boolean undirected = !navigableLeft && !navigableRight;

    if (navigableRight || undirected) {
      references.add(new AssociationReference(findTypeByFullName(assoc, assoc.getLeftQualifiedName()
          .getQName()), deriveReferenceName(assoc, AssocSide.RIGHT)));
    }
    if (navigableLeft || undirected) {
      references.add(new AssociationReference(findTypeByFullName(assoc, assoc
          .getRightQualifiedName().getQName()), deriveReferenceName(assoc, AssocSide.LEFT)));
    }

    return references;
  }

  protected String deriveReferenceName(ASTCDAssociation assoc, AssocSide side) {
    ASTCDAssocSide assocSide;
    if (side.equals(AssocSide.LEFT)) {
      assocSide = assoc.getLeft();
    }
    else {
      assocSide = assoc.getRight();
    }
    if (assoc.isPresentName()) {
      return StringUtils.uncapitalize(assoc.getName());
    }
    else if (assocSide.isPresentCDRole()) {
      return assocSide.getCDRole().getName();
    }
    else {
      return StringUtils.uncapitalize(assocSide.getMCQualifiedType().getMCQualifiedName()
          .getBaseName());
    }
  }

  protected enum AssocSide {
    LEFT, RIGHT;
  }

  protected static class AssociationReference {

    protected final ASTCDType sourceType;

    protected final String name;

    protected AssociationReference(ASTCDType sourceType, String name) {
      this.sourceType = sourceType;
      this.name = name;
    }

  }

}
