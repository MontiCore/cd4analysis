package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo;

/**
 * Checks that association names start lower-case.
 *
 * @author Robert Heim
 */
public class AssociationNameLowerCase implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A16";
  
  public static final String ERROR_MSG_FORMAT = "Association %s must start in lower-case.";
  
  @Override
  public void check(ASTCDAssociation a) {
    if (a.getName().isPresent()) {
      if (!Character.isLowerCase(a.getName().get().charAt(0))) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, a.getName().get()),
            a.get_SourcePositionStart());
      }
    }
  }
}
