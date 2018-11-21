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

package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that role names do not conflict with other role names where the source
 * types has other outgoing associations; it only considers the specification mode of associations
 * -> the rest should be covered by AssociationRoleNameNoConflictWithOtherRoleNames
 * @author Michael von Wenckstern
 */
public class AssociationRoleNameNoConflictWithOtherRoleNamesSpecMode implements
    CD4AnalysisASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {
    Optional<CDAssociationSymbol> error = Optional.empty();
    error = check(a.getLeftToRightSymbol());
    if (!error.isPresent()) {
      error = check(a.getRightToLeftSymbol());
    }
  }

  // true for error
  private Optional<CDAssociationSymbol> check(Optional<CDAssociationSymbol> assSymbol) {
    if (!assSymbol.isPresent()) {
      return Optional.empty();
    }
    Optional<CDAssociationSymbol> ret = Optional.empty();
    ret = check(assSymbol.get().getSourceType(), assSymbol.get().getTargetRole(), assSymbol.get());
    if (!ret.isPresent()) {
      ret = check(assSymbol.get().getTargetType(), assSymbol.get().getSourceRole(), assSymbol.get());
    }
    return ret;
  }

  private Optional<CDAssociationSymbol> check(CDTypeSymbol type, Optional<String> optName, CDAssociationSymbol assSymbol) {
    if (!optName.isPresent()) {
      return Optional.empty();
    }
    String name = optName.get();
    // compare ASTNode and not symbol, because for bidirectional ASTNodes two single directional symbols are created
    List<CDAssociationSymbol> list = new ArrayList<>();
    if (type instanceof CDTypeSymbolReference) {
      list.addAll(((CDTypeSymbolReference) type).getReferencedSymbol().getSpecAssociations().stream().map(s -> s.getInverseAssociation()).collect(Collectors.toList()));
    } else {
      list.addAll(type.getSpecAssociations());
    }
    Optional<CDAssociationSymbol> error = list.stream().
            filter(ass -> !ass.getAstNode().equals(assSymbol.getAstNode()))
            .filter(ass -> ass.getTargetType().getFullName().equals(type.getFullName()) ? ass.getSourceRole().equals(optName) : ass.getTargetRole().equals(optName)).findAny();

    if (error.isPresent()) {
      ASTCDAssociation a = (ASTCDAssociation)assSymbol.getAstNode().get();
      Log.error(
              String.format("0xC4A39 Role namespace clash `%s::%s` of associations `%s` and `%s`.",
                      type.getName(), name, a, error.get().getAstNode().isPresent() ? error.get().getAstNode().get() : error.get()),
              a.get_SourcePositionStart());
    }

    return error;
  }
}