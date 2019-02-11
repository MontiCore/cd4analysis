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

import static de.monticore.types.types._ast.TypesNodeFactory.createASTQualifiedName;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
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
