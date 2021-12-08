/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that association names do not conflict with attributes in source
 * types.
 */
public class CDAssociationHasSymbol
    implements CDAssociationASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {
    if (a.isPresentName() && !a.isPresentSymbol()) {
      final String assocName = a.getName();

      Log.error(String.format("0xCDC62: Association %s has no associated symbol, please create a symbol table.",
          assocName),
          a.get_SourcePositionStart());
    }
  }
}
