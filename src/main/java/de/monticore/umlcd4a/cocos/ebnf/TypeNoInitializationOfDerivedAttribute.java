package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;

/**
 * Checks that derived attributes are not initialized.
 *
 * @author Robert Heim
 */
public class TypeNoInitializationOfDerivedAttribute implements CD4AnalysisASTCDAttributeCoCo {
  
  public static final String ERROR_CODE = "0xC4A34";
  
  public static final String ERROR_MSG_FORMAT = "Invalid initialization of the derived attribute %s. Derived attributes may not be initialized.";
  
  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.getModifier().isPresent()) {
      if (attr.getModifier().get().isDerived()) {
        if (attr.getValue().isPresent()) {
          CoCoLog.error(ERROR_CODE,
              String.format(ERROR_MSG_FORMAT, attr.getName()),
              attr.get_SourcePositionStart());
        }
      }
    }
  }
}
