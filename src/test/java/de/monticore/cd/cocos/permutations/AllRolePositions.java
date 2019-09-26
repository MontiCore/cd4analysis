/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class AllRolePositions implements
    Function<Permutation<ASTCDAssociation>, Set<Permutation<ASTCDAssociation>>> {
  
  private static final String leftRole = "leftRole";
  
  private static final String rightRole = "rightRole";
  
  @Override
  public Set<Permutation<ASTCDAssociation>> apply(Permutation<ASTCDAssociation> assocPermutation) {
    Set<Permutation<ASTCDAssociation>> successorPermutations = new LinkedHashSet<>();
    
    if (assocPermutation.delegate().isBidirectional() || assocPermutation.delegate().isUnspecified()) {
      successorPermutations.addAll(bidirectionalOrSimple(assocPermutation));
    }
    if (assocPermutation.delegate().isLeftToRight()) {
      successorPermutations.add(leftToRight(assocPermutation));
    }
    if (assocPermutation.delegate().isRightToLeft()) {
      successorPermutations.add(rightToLeft(assocPermutation));
    }
    
    return successorPermutations;
  }
  
  private Set<Permutation<ASTCDAssociation>> bidirectionalOrSimple(
      Permutation<ASTCDAssociation> assocPermutation) {
    Set<Permutation<ASTCDAssociation>> successorPermutations = new LinkedHashSet<>();
    
    Permutation<ASTCDAssociation> bothDirections = assocPermutation.copy();
    bothDirections.delegate().setLeftRole(leftRole);
    bothDirections.delegate().setRightRole(rightRole);
    successorPermutations.add(bothDirections);
    
    successorPermutations.add(leftToRight(assocPermutation));
    successorPermutations.add(rightToLeft(assocPermutation));
    
    return successorPermutations;
  }
  
  private Permutation<ASTCDAssociation> leftToRight(Permutation<ASTCDAssociation> assocPermutation) {
    Permutation<ASTCDAssociation> rightOnly = assocPermutation.copy();
    rightOnly.delegate().setRightRole(rightRole);
    return rightOnly;
  }
  
  private Permutation<ASTCDAssociation> rightToLeft(Permutation<ASTCDAssociation> assocPermutation) {
    Permutation<ASTCDAssociation> leftOnly = assocPermutation.copy();
    leftOnly.delegate().setLeftRole(leftRole);
    return leftOnly;
  }
}
