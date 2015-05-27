package de.monticore.umlcd4a.cocos.permutations;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;

public class AllQualifierPositions implements
    Function<Permutation<ASTCDAssociation>, Set<Permutation<ASTCDAssociation>>> {
  
  private final ASTCDQualifier leftQualifier = CD4AnalysisNodeFactory.createASTCDQualifier();
  
  private final ASTCDQualifier rightQualifier = CD4AnalysisNodeFactory.createASTCDQualifier();
  
  public AllQualifierPositions(String leftQualifierName, String rightQualifierName) {
    leftQualifier.setName(leftQualifierName);
    rightQualifier.setName(rightQualifierName);
  }
  
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
    bothDirections.delegate().setLeftQualifier(leftQualifier);
    bothDirections.delegate().setRightQualifier(rightQualifier);
    successorPermutations.add(bothDirections);
    
    successorPermutations.add(leftToRight(assocPermutation));
    successorPermutations.add(rightToLeft(assocPermutation));
    
    return successorPermutations;
  }
  
  private Permutation<ASTCDAssociation> leftToRight(Permutation<ASTCDAssociation> assocPermutation) {
    Permutation<ASTCDAssociation> rightOnly = assocPermutation.copy();
    rightOnly.delegate().setRightQualifier(rightQualifier);
    return rightOnly;
  }
  
  private Permutation<ASTCDAssociation> rightToLeft(Permutation<ASTCDAssociation> assocPermutation) {
    Permutation<ASTCDAssociation> leftOnly = assocPermutation.copy();
    leftOnly.delegate().setLeftQualifier(leftQualifier);
    return leftOnly;
  }
}
