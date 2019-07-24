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
import de.se_rwth.commons.StringTransformations;

import java.util.function.Function;

public class CapitalizeRoles implements
    Function<Permutation<ASTCDAssociation>, Permutation<ASTCDAssociation>> {
  
  @Override
  public Permutation<ASTCDAssociation> apply(Permutation<ASTCDAssociation> assocPermutation) {
    Permutation<ASTCDAssociation> successorPermutation = assocPermutation.copy();
    ASTCDAssociation association = successorPermutation.delegate();
    
    if (association.isPresentLeftRole()) {
      association.setLeftRole(StringTransformations.capitalize(association.getLeftRole()));
    }
    if (association.isPresentRightRole()) {
      association
          .setRightRole(StringTransformations.capitalize(association.getRightRole()));
    }
    return successorPermutation;
  }
  
}