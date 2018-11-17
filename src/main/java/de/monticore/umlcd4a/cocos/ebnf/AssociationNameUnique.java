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

import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.logging.Log;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that association names are unique in the namespace.
 *
 * @author Michael von Wenckstern
 */
public class AssociationNameUnique implements CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation a) {
    if (!a.getNameOpt().isPresent()) {
      return;
    }

    Optional<CDAssociationSymbol> error = Optional.empty();
    error = check(a.getLeftToRightSymbol(), a.getName());
    if (!error.isPresent()) {
      error = check(a.getRightToLeftSymbol(), a.getName());
    }
    /*if (error.isPresent()) {
      Log.error(
              String.format("0xC4A26 Association name `%s` of association %s is already defined in `%s`.",
                      a.getName(), a, error.get().getAstNode().isPresent() ? error.get().getAstNode().get() : error.get()),
              a.get_SourcePositionStart());
    }*/

  }

  // true for error
  private Optional<CDAssociationSymbol> check(Optional<CDAssociationSymbol> assSymbol, String name) {
    if (!assSymbol.isPresent()) {
      return Optional.empty();
    }
    Optional<CDAssociationSymbol> ret = Optional.empty();
    ret = check(assSymbol.get().getSourceType(), name, assSymbol.get());
    if (!ret.isPresent()) {
      ret = check(assSymbol.get().getTargetType(), name, assSymbol.get());
    }
    return ret;
  }

  private Optional<CDAssociationSymbol> check(CDTypeSymbol type, String name, CDAssociationSymbol assSymbol) {
    // compare ASTNode and not symbol, because for bidirectional ASTNodes two single directional symbols are created
    List<CDAssociationSymbol> list = new ArrayList<>(type.getAssociations());
    if (type instanceof CDTypeSymbolReference) {
      list.addAll(((CDTypeSymbolReference) type).getReferencedSymbol().getSpecAssociations());
    } else {
      list.addAll(type.getSpecAssociations());
    }
    Optional<CDAssociationSymbol> error = list.stream().
            filter(ass -> !ass.getAstNode().equals(assSymbol.getAstNode()) && ass.getDerivedName().equals(name)).findAny();

    if (error.isPresent()) {
      ASTCDAssociation a = (ASTCDAssociation)assSymbol.getAstNode().get();
      Log.error(
              String.format("0xC4A26 Association namespace clash `%s::%s` of associations `%s` and `%s`.",
                      type.getName(), a.getName(), a, error.get().getAstNode().isPresent() ? error.get().getAstNode().get() : error.get()),
              a.get_SourcePositionStart());
    }

    return error;
  }

     /*
      try {
        a.getEnclosingScope().resolve(a.getName(), CDAssociationSymbol.KIND);
      }
      catch (ResolvedSeveralEntriesException e) {
        // TODO currently there exist two symbols for bidirectional
        // associations, see #1627
        boolean isValid = (a.isBidirectional() || a.isUnspecified()) && e.getSymbols().size() == 2;
        if (!isValid) {
          Log.error(
              String.format("0xC4A26 Association %s is defined multiple times.", a.getName()),
              a.get_SourcePositionStart());
        }
      }
    }
  }*/
}
