package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that attribute names start lower-case.
 *
 * @author Robert Heim
 */
public class AttributeNameLowerCase implements CD4AnalysisASTCDAttributeCoCo {
  
  @Override
  public void check(ASTCDAttribute a) {
    if (!Character.isLowerCase(a.getName().charAt(0))) {
      Log.error(String.format("0xC4A12 Attribute %s must start in lower-case.", a.getName()),
          a.get_SourcePositionStart());
    }
  }
}
