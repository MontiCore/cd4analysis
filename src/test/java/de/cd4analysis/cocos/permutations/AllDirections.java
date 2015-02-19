package de.cd4analysis.cocos.permutations;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import de.cd4analysis._ast.ASTCDAssociation;

public class AllDirections implements
    Function<Permutation<ASTCDAssociation>, Set<Permutation<ASTCDAssociation>>> {
  
  @Override
  public Set<Permutation<ASTCDAssociation>> apply(Permutation<ASTCDAssociation> assocPermutation) {
    Set<Permutation<ASTCDAssociation>> successorPermutations = new LinkedHashSet<>();
    
    // ->
    Permutation<ASTCDAssociation> leftToRight = assocPermutation.copy();
    leftToRight.delegate().setLeftToRight(true);
    successorPermutations.add(leftToRight);
    
    // <-
    Permutation<ASTCDAssociation> rightToLeft = assocPermutation.copy();
    rightToLeft.delegate().setRightToLeft(true);
    successorPermutations.add(rightToLeft);
    
    // <->
    Permutation<ASTCDAssociation> bidirectional = assocPermutation.copy();
    bidirectional.delegate().setBidirectional(true);
    successorPermutations.add(bidirectional);
    
    // --
    Permutation<ASTCDAssociation> simple = assocPermutation.copy();
    simple.delegate().setSimple(true);
    successorPermutations.add(simple);
    
    return successorPermutations;
  }
  
}