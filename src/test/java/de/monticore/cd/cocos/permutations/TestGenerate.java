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

import de.monticore.ast.ASTNode;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.ASTNodes;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sebastian Oberhoff
 */
public class TestGenerate {
  
  @Test
  @Ignore
  public void runTest() {
    Permutation<ASTCDAssociation> assocPermutation = createDefaultAssociation();
    
    Set<Permutation<ASTCDAssociation>> permutations = Stream.of(assocPermutation)
        .flatMap(new CreateInheritanceTree().andThen(Set::stream))
        // .map(new SetAttributeNamesEqualAssociationNames())
        // .map(new SetAttributeNamesEqualRoleNames())
        // .map(new SetAttributeNameEqualsTargetType())
        .collect(Collectors.toSet());
    
    print(permutations);
  }
  
  private Permutation<ASTCDAssociation> createDefaultAssociation() {
    ASTCDAssociation cdAssociation = CD4AnalysisNodeFactory.createASTCDAssociation();
    ASTMCQualifiedName leftReferenceName = CD4AnalysisNodeFactory.createASTMCQualifiedName(Arrays.asList("LeftType"));
    cdAssociation.setLeftReferenceName(leftReferenceName);
    ASTMCQualifiedName rightReferenceName = CD4AnalysisNodeFactory.createASTMCQualifiedName(Arrays.asList("RightType"));
    cdAssociation.setRightReferenceName(rightReferenceName);
    cdAssociation.setUnspecified(true);
    
    Permutation<ASTCDAssociation> assocPermutation = new Permutation<>(cdAssociation);
    assocPermutation.addIdSetter(this::setReferenceNameId);
    return assocPermutation;
  }
  
  private void print(Set<Permutation<ASTCDAssociation>> permutations) {
    int index = 1;
    for (Permutation<ASTCDAssociation> permutation : permutations) {
      permutation.applyIdSetters("" + index);
      System.out.println(permutation);
      System.out.println("------");
      index++;
    }
  }
  
  private void setReferenceNameId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTQualifiedName.class))
        .flatMap(Collection::stream)
        .forEach(qualifiedName -> {
          String oldName = qualifiedName.getPartList().get(0);
          qualifiedName.getPartList().set(0, oldName + id);
        });
  }
  
}
