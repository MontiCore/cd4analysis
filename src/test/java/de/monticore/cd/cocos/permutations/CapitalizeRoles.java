/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.se_rwth.commons.StringTransformations;

import java.util.function.Function;

public class CapitalizeRoles implements
    Function<Permutation<ASTCDAssociation>, Permutation<ASTCDAssociation>> {

  @Override
  public Permutation<ASTCDAssociation> apply(Permutation<ASTCDAssociation> assocPermutation) {
    Permutation<ASTCDAssociation> successorPermutation = assocPermutation.copy();
    ASTCDAssociation association = successorPermutation.delegate();

    if (association.isPresentLeftRole()) {
      association.getLeftRole()
          .setName(StringTransformations.capitalize(association.getLeftRole().getName()));
    }
    if (association.isPresentRightRole()) {
      association.getRightRole()
          .setName(StringTransformations.capitalize(association.getRightRole().getName()));
    }
    return successorPermutation;
  }

}
