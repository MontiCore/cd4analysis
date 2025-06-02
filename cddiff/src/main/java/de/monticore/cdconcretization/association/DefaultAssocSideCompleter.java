/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdconcretization.CompletionException;

public class DefaultAssocSideCompleter implements IAssocSideCompleter {
  
  protected boolean intersectCardinality = false; // TODO make configurable
  
  @Override
  public void completeAssocSide(ASTCDAssocSide concreteAssocSide, ASTCDAssocSide referenceAssocSide)
      throws CompletionException {
    completeAssocCardinality(concreteAssocSide, referenceAssocSide);
    completeAssociationRoleNames(concreteAssocSide, referenceAssocSide);
  }
  
  private void completeAssocCardinality(ASTCDAssocSide cAssocSide, ASTCDAssocSide rAssocSide)
      throws CompletionException {
    if (!cAssocSide.isPresentCDCardinality() && rAssocSide.isPresentCDCardinality()) {
      cAssocSide.setCDCardinality(rAssocSide.getCDCardinality());
    }
    else if (cAssocSide.isPresentCDCardinality() && rAssocSide.isPresentCDCardinality()
        && !cAssocSide.getCDCardinality().deepEquals(rAssocSide.getCDCardinality())) {
      if (!intersectCardinality) {
        throw new CompletionException("Unequal cardinalities");
      }
      else {
        // todo: schnitt von cardinalitäten
      }
    }
  }
  
  private void completeAssociationRoleNames(ASTCDAssocSide cAssocSide, ASTCDAssocSide rAssocSide) {
    if (!cAssocSide.isPresentCDRole() && rAssocSide.isPresentCDRole()) {
      cAssocSide.setCDRole(rAssocSide.getCDRole());
    }
  }
  
}
