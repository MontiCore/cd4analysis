package de.monticore.umlcd4a.cocos.permutations;

import static de.monticore.types._ast.TypesNodeFactory.createASTQualifiedName;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a._ast.ASTCDAssociation;

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
      ASTQualifiedName leftReferenceName = createASTQualifiedName(Arrays.asList(leftType));
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
      ASTQualifiedName rightReferenceName = createASTQualifiedName(Arrays.asList(leftType));
      copy.delegate().setRightReferenceName(rightReferenceName);
      rightTypedAssocs.add(copy);
    }
    return rightTypedAssocs;
  }
}
