/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that association names start lower-case.
 *
 * @author Robert Heim
 */
public class AssociationNameLowerCase implements CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation a) {
    if (a.isPresentName()) {
      if (!Character.isLowerCase(a.getName().charAt(0))) {
        Log.error(
            String.format("0xC4A16 Association %s must start in lower-case.", a.getName()),
            a.get_SourcePositionStart());
      }
    }
  }
}
