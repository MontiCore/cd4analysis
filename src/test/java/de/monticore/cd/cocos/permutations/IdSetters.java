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
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCDField;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.utils.ASTNodes;

import java.util.Collection;
import java.util.Optional;

class IdSetters {
  
  static void setRoleId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> {
          cdAssociation.setLeftRole(cdAssociation.getLeftRole() + id);
          cdAssociation.setRightRole(cdAssociation.getRightRole() + id);
        });
  }
  
  static void setAssocId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> {
          cdAssociation.setName(cdAssociation.getName() + id);
        });
  }
  
  static void setAttributeId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDField.class))
        .flatMap(Collection::stream)
        .forEach(cdAttribute -> cdAttribute.setName(cdAttribute.getName() + id));
  }
  
  static void setSuperClassNameId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDClass.class))
        .flatMap(Collection::stream)
        .map(ASTCDClass::getSuperclassOpt)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(ASTSimpleReferenceType.class::isInstance)
        .map(ASTSimpleReferenceType.class::cast)
        .forEach(simpleReferenceType -> {
          String oldName = simpleReferenceType.getNameList().get(0);
          simpleReferenceType.getNameList().set(0, oldName + id);
        });
  }
  
  static void setClassNameId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDClass.class))
        .flatMap(Collection::stream)
        .forEach(cdClass -> cdClass.setName(cdClass.getName() + id));
  }
}
