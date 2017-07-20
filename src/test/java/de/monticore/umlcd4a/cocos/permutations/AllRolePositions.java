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

package de.monticore.umlcd4a.cocos.permutations;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;

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
