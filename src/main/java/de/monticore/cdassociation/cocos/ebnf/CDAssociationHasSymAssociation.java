/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that association names do not conflict with attributes in source
 * types.
 */
public class CDAssociationHasSymAssociation
    implements CDAssociationASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {
    if (!a.isPresentSymAssociation()) {
      Log.error("0xCDC60: Association has no SymAssociation.",
          a.get_SourcePositionStart());
    }
  }
}
