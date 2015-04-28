package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo;

/**
 * Checks that attribute names start lower-case.
 *
 * @author Robert Heim
 */
public class AttributeNameLowerCase implements CD4AnalysisASTCDAttributeCoCo {
  
  public static final String ERROR_CODE = "0xC4A12";
  
  public static final String ERROR_MSG_FORMAT = "Attribute %s must start in lower-case.";
  
  @Override
  public void check(ASTCDAttribute a) {
    if (!Character.isLowerCase(a.getName().charAt(0))) {
      CoCoLog.error(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, a.getName()),
          a.get_SourcePositionStart());
    }
  }
}
