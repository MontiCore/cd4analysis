/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCardinality;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class SetCardinality implements
    Function<Permutation<ASTCDAssociation>, Set<Permutation<ASTCDAssociation>>> {

  @Override
  public Set<Permutation<ASTCDAssociation>> apply(Permutation<ASTCDAssociation> assocPermutation) {

    List<ASTCardinality> cardinalities = createCardinalities();

    Set<Permutation<ASTCDAssociation>> successorPermutations = Permuter.permute(
        this::setLeftCardinality,
        Collections.singleton(assocPermutation), cardinalities);

    successorPermutations = Permuter.permute(this::setRightCardinality, successorPermutations,
        cardinalities);

    return successorPermutations;
  }

  private List<ASTCardinality> createCardinalities() {
    List<ASTCardinality> cardinalities = new ArrayList<>();

    ASTCardinality optional = CD4AnalysisNodeFactory.createASTCardinality();
    optional.setOptional(true);
    cardinalities.add(optional);

    ASTCardinality oneToOne = CD4AnalysisNodeFactory.createASTCardinality();
    oneToOne.setOne(true);
    cardinalities.add(oneToOne);

    ASTCardinality oneToMany = CD4AnalysisNodeFactory.createASTCardinality();
    oneToMany.setOneToMany(true);
    cardinalities.add(oneToMany);

    ASTCardinality manyToMany = CD4AnalysisNodeFactory.createASTCardinality();
    manyToMany.setMany(true);
    cardinalities.add(manyToMany);

    return cardinalities;
  }

  private Permutation<ASTCDAssociation> setLeftCardinality(
      Permutation<ASTCDAssociation> assocPermutation,
      ASTCardinality cardinality) {
    Permutation<ASTCDAssociation> copy = assocPermutation.copy();
    copy.delegate().setLeftCardinality(cardinality);
    return copy;
  }

  private Permutation<ASTCDAssociation> setRightCardinality(
      Permutation<ASTCDAssociation> assocPermutation,
      ASTCardinality cardinality) {
    Permutation<ASTCDAssociation> copy = assocPermutation.copy();
    copy.delegate().setRightCardinality(cardinality);
    return copy;
  }
  
}
