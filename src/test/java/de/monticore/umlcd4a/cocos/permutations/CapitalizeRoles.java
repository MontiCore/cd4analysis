package de.monticore.umlcd4a.cocos.permutations;

import java.util.function.Function;

import de.cd4analysis._ast.ASTCDAssociation;
import de.se_rwth.commons.StringTransformations;

public class CapitalizeRoles implements
    Function<Permutation<ASTCDAssociation>, Permutation<ASTCDAssociation>> {
  
  @Override
  public Permutation<ASTCDAssociation> apply(Permutation<ASTCDAssociation> assocPermutation) {
    Permutation<ASTCDAssociation> successorPermutation = assocPermutation.copy();
    ASTCDAssociation association = successorPermutation.delegate();
    
    if (association.getLeftRole().isPresent()) {
      association.setLeftRole(StringTransformations.capitalize(association.getLeftRole().get()));
    }
    if (association.getRightRole().isPresent()) {
      association
          .setRightRole(StringTransformations.capitalize(association.getRightRole().get()));
    }
    return successorPermutation;
  }
  
}
