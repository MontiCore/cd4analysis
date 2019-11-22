/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that role names do not conflict with other role names where the source
 * types has other outgoing associations; it only considers the specification mode of associations
 * the rest should be covered by AssociationRoleNameNoConflictWithOtherRoleNames
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
    ret = check(assSymbol.get().getSourceType().getLoadedSymbol(),
            assSymbol.get().getTargetRole(), assSymbol.get());
    if (!ret.isPresent()) {
      ret = check(assSymbol.get().getTargetType().getLoadedSymbol(),
              assSymbol.get().getSourceRole(), assSymbol.get());
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
    list.addAll(type.getSpecAssociations());
    Optional<CDAssociationSymbol> error = Optional.empty();
    for (CDAssociationSymbol ass : list) {
      if (!ass.getAstNode().equals(assSymbol.getAstNode())) {
        if (ass.getTargetType().getLoadedSymbol().getFullName().equals(type.getFullName())) {
          if (ass.isPresentSourceRole() && ass.getSourceRole().equals(name)) {
            error = Optional.of(ass);
            break;
          }
        } else if (ass.isPresentTargetRole() && ass.getTargetRole().equals(name)) {
          error = Optional.of(ass);
          break;
        }
      }
    }

    if (error.isPresent()) {
      ASTCDAssociation a = (ASTCDAssociation)assSymbol.getAstNode();
      Log.error(
              String.format("0xC4A39 Role namespace clash `%s::%s` of associations `%s` and `%s`.",
                      type.getName(), name, a, error.get().isPresentAstNode() ? error.get().getAstNode() : error.get()),
              a.get_SourcePositionStart());
    }

    return error;
  }
}
