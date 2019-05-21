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

import com.google.common.collect.Iterables;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCDField;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateInheritanceTree implements
    Function<Permutation<ASTCDAssociation>, Set<Permutation<ASTCDAssociation>>> {
  
  @Override
  public Set<Permutation<ASTCDAssociation>> apply(Permutation<ASTCDAssociation> testPermutation) {
    
    Set<Permutation<ASTCDAssociation>> successorPermutations = new LinkedHashSet<>();
    for (int inheritanceDepth = 0; inheritanceDepth < 4; inheritanceDepth++) {
      successorPermutations.add(createInheritanceClash(testPermutation, inheritanceDepth));
    }
    return successorPermutations;
  }
  
  private Permutation<ASTCDAssociation> createInheritanceClash(
      Permutation<ASTCDAssociation> assocPermutation,
      int inheritanceDepth) {
    Permutation<ASTCDAssociation> successorPermutation = assocPermutation.copy();
    
    ASTCDClass referencedClass = createReferencedClass(successorPermutation.delegate()
        .getLeftReferenceName().getPartList().get(0));
    List<ASTCDClass> superClasses = createSuperClasses(referencedClass, inheritanceDepth);
    ASTCDClass topClass = !superClasses.isEmpty()
        ? Iterables.getLast(superClasses)
        : referencedClass;
    addAttributeToTopClass(topClass);
    
    successorPermutation.addAstNode(referencedClass);
    successorPermutation.addAstNodes(superClasses);
    
    successorPermutation.addIdSetter(IdSetters::setClassNameId);
    successorPermutation.addIdSetter(IdSetters::setSuperClassNameId);
    
    return successorPermutation;
  }
  
  private ASTCDClass createReferencedClass(String referencedClassName) {
    ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
    cdClass.setName(referencedClassName);
    return cdClass;
  }
  
  private List<ASTCDClass> createSuperClasses(ASTCDClass baseClass, int inheritanceDepth) {
    return Stream
        .iterate(baseClass, CreateInheritanceTree::newSuperClass)
        .skip(1)
        .limit(inheritanceDepth)
        .collect(Collectors.toList());
  }
  
  private static void addAttributeToTopClass(ASTCDClass topClass) {
    ASTCDField cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    topClass.getCDAttributeList().add(cdAttribute);
  }
  
  private static ASTCDClass newSuperClass(ASTCDClass subClass) {
    ASTCDClass superClass = CD4AnalysisNodeFactory.createASTCDClass();
    superClass.setName("Super" + subClass.getName());
    subClass.setSuperclass(createSimpleReference(superClass));
    return superClass;
  }
  
  private static ASTMCQualifiedType createSimpleReference(ASTCDClass superClass) {
    ASTMCQualifiedType reference = CD4AnalysisNodeFactory.createASTMCQualifiedType();
    ArrayList<String> name = new ArrayList<String>();
    name.add(superClass.getName());
    reference.getMCQualifiedName().setPartList(name);
    return reference;
  }
  
}
