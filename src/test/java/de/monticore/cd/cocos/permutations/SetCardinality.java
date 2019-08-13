/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
