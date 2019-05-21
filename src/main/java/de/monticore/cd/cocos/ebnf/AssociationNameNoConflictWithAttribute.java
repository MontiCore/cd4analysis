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

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that association names do not conflict with attributes in source
 * types.
 *
 * @author Robert Heim
 */
public class AssociationNameNoConflictWithAttribute implements CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation a) {
    if (a.isPresentName()) {
      String assocName = a.getName();
      Optional<CDTypeSymbol> leftType = a.getEnclosingScope2()
          .resolveCDType(a.getLeftReferenceName().toString());
      Optional<CDTypeSymbol> rightType = a.getEnclosingScope2()
          .resolveCDType(a.getRightReferenceName().toString());
      boolean err = false;
      // source type might be external (in this case we do nothing)
      if (leftType.isPresent() && (a.isLeftToRight() || a.isBidirectional() || a.isUnspecified())) {
        err = check(leftType.get(), rightType, assocName, a);
      }
      if (rightType.isPresent() && !err
          && (a.isRightToLeft() || a.isBidirectional() || a.isUnspecified())) {
        check(rightType.get(), leftType, assocName, a);
      }
    }
  }
  
  /**
   * Does the actual check.
   * 
   * @param sourceType source of the assoc under test
   * @param assocName the associations name
   * @param assoc association under test
   * @return whether there was a CoCo error or not.
   */
  private boolean check(CDTypeSymbol sourceType, Optional<CDTypeSymbol> targetType, String assocName, ASTCDAssociation assoc) {
    // attributes
    Optional<CDFieldSymbol> conflictingAttribute = sourceType.getAllVisibleFields().stream()
        .filter(f -> f.getName().equals(assocName))
        .findAny();
    
    if (conflictingAttribute.isPresent()) {
      error(assocName, conflictingAttribute.get().getEnclosingScope()
          .getSpanningSymbol().get().getName(), assoc);
      return true;
    }
    
    // automatically introduced attributes from other assocs of the source type
    // that are not defined by assoc name (same assoc name would be found by
    // other coco)
    Optional<CDAssociationSymbol> conflictingAssoc = sourceType.getAssociations().stream()
        .filter(a -> !a.getAssocName().isPresent())
        .filter(a -> a.getDerivedName().equals(assocName))
        .findAny();
    if (!conflictingAssoc.isPresent()) {
      // automatically introduced attributes from inherited associations
      conflictingAssoc = sourceType
          .getInheritedAssociations().stream()
          .filter(a -> a.getDerivedName().equals(assocName))
          .findAny();
    }
    if (conflictingAssoc.isPresent()) {
      boolean isReadOnly = false;
      List<String> superTypes = null;
      if (targetType.isPresent()) {
        isReadOnly = ((ASTCDAssociation) (conflictingAssoc.get().getAstNode().get())).isReadOnly();
        superTypes = targetType.get().getSuperTypes().stream().map(type -> type.getFullName()).collect(Collectors.toList());
      }
      if (isReadOnly && superTypes.contains(conflictingAssoc.get().getTargetType().getFullName())) {
        Log.info(String.format("Association `%s` overwrites read-only association `%s`",
                assoc, conflictingAssoc.get()),
                "INFO");
      }
      else {
        error(assocName,
                conflictingAssoc.get().getSourceType().getName(),
                assoc);
        return true;
      }
    }
    return false;
  }
  
  private void error(String assocName, String typeName, ASTCDAssociation assoc) {
    Log.error(String.format("0xC4A25 Association %s conflicts with the attribute %s in %s.",
        assocName, assocName, typeName),
        assoc.get_SourcePositionStart());
  }
}
