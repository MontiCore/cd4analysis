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
 * Checks that association names start lower-case.
 */
public class CDAssociationNameLowerCase
    implements CDAssociationASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {
    if (a.isPresentName()) {
      if (!Character.isLowerCase(a.getName().charAt(0))) {
        Log.error(
            String.format("0xCDC61: Association %s must start in lower-case.", a.getName()),
            a.get_SourcePositionStart());
      }
    }
  }
}
