/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AllTypeCombinations implements
    Function<Permutation<ASTCDAssociation>, Set<Permutation<ASTCDAssociation>>> {
  
  @Override
  public Set<Permutation<ASTCDAssociation>> apply(Permutation<ASTCDAssociation> assocPermutation) {
    return allLeftTypes(assocPermutation).stream()
        .map(this::allRightTypes)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }
  
  private Set<Permutation<ASTCDAssociation>> allLeftTypes(
      Permutation<ASTCDAssociation> assocPermutation) {
    Set<Permutation<ASTCDAssociation>> leftTypedAssocs = new LinkedHashSet<>();
    // A = class, B = class, E = enum, I = interface
    for (String leftType : Arrays.asList("A", "B", "E", "I")) {
      Permutation<ASTCDAssociation> copy = assocPermutation.copy();
      ASTMCQualifiedName leftReferenceName =
              CD4AnalysisMill.mCQualifiedNameBuilder().setPartList(Arrays.asList(leftType)).build();
      copy.delegate().setLeftReferenceName(leftReferenceName);
      leftTypedAssocs.add(copy);
    }
    return leftTypedAssocs;
  }
  
  private Set<Permutation<ASTCDAssociation>> allRightTypes(
      Permutation<ASTCDAssociation> assocPermutation) {
    Set<Permutation<ASTCDAssociation>> rightTypedAssocs = new LinkedHashSet<>();
    // A = class, B = class, E = enum, I = interface
    for (String leftType : Arrays.asList("A", "B", "E", "I")) {
      Permutation<ASTCDAssociation> copy = assocPermutation.copy();
      ASTMCQualifiedName rightReferenceName =
              CD4AnalysisMill.mCQualifiedNameBuilder().setPartList(Arrays.asList(leftType)).build();
      copy.delegate().setRightReferenceName(rightReferenceName);
      rightTypedAssocs.add(copy);
    }
    return rightTypedAssocs;
  }
}
