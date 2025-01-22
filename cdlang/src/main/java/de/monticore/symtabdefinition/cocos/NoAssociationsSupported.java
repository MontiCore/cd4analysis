// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinition.cocos;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

public class NoAssociationsSupported implements CDAssociationASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation node) {
    Log.error(
        "0xFDC17 encountered an association." + " Associations are not supported.",
        node.get_SourcePositionStart(),
        node.get_SourcePositionStart());
  }
}
