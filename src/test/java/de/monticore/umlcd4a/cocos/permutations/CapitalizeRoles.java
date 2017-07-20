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

import java.util.function.Function;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.se_rwth.commons.StringTransformations;

public class CapitalizeRoles implements
    Function<Permutation<ASTCDAssociation>, Permutation<ASTCDAssociation>> {
  
  @Override
  public Permutation<ASTCDAssociation> apply(Permutation<ASTCDAssociation> assocPermutation) {
    Permutation<ASTCDAssociation> successorPermutation = assocPermutation.copy();
    ASTCDAssociation association = successorPermutation.delegate();
    
    if (association.getLeftRole().isPresent()) {
      association.setLeftRole(StringTransformations.capitalize(association.getLeftRole().get()));
    }
    if (association.getRightRole().isPresent()) {
      association
          .setRightRole(StringTransformations.capitalize(association.getRightRole().get()));
    }
    return successorPermutation;
  }
  
}
