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

import java.util.Collection;
import java.util.function.UnaryOperator;

import de.monticore.ast.ASTNode;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.utils.ASTNodes;

public class SetAttributeNameEqualsTargetType implements
    UnaryOperator<Permutation<ASTCDAssociation>> {
  
  @Override
  public Permutation<ASTCDAssociation> apply(Permutation<ASTCDAssociation> assocPermutation) {
    setAttributeName(assocPermutation);
    
    assocPermutation.addIdSetter(IdSetters::setAttributeId);
    
    return assocPermutation;
  }
  
  private void setAttributeName(Permutation<ASTCDAssociation> assocPermutation) {
    assocPermutation
        .getAstNodes()
        .stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAttribute.class))
        .flatMap(Collection::stream)
        .forEach(
            cdAttribute -> cdAttribute.setName(determineTypeName(cdAttribute, assocPermutation)));
  }
  
  private String determineTypeName(ASTCDAttribute cdAttribute,
      Permutation<ASTCDAssociation> assocPermutation) {
    if (attributeIsOnLeftSide(cdAttribute, assocPermutation)) {
      return assocPermutation.delegate().getRightReferenceName().getParts().get(0);
    }
    else {
      return assocPermutation.delegate().getLeftReferenceName().getParts().get(0);
    }
  }
  
  private boolean attributeIsOnLeftSide(ASTCDAttribute cdAttribute,
      Permutation<ASTCDAssociation> assocPermutation) {
    for (ASTNode astNode : assocPermutation.getAstNodes()) {
      boolean hasContainingClassOnLeft = ASTNodes.getIntermediates(astNode, cdAttribute).stream()
          .filter(ASTCDClass.class::isInstance)
          .map(ASTCDClass.class::cast)
          .map(ASTCDClass::getName)
          .anyMatch(name -> name.contains("Left"));
      if (hasContainingClassOnLeft) {
        return true;
      }
    }
    return false;
  }
}
