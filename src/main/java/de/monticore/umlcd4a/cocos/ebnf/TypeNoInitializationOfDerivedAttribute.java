package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that derived attributes are not initialized.
 *
 * @author Robert Heim
 */
public class TypeNoInitializationOfDerivedAttribute implements CD4AnalysisASTCDAttributeCoCo {
  
  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.getModifier().isPresent()) {
      if (attr.getModifier().get().isDerived()) {
        if (attr.getValue().isPresent()) {
          Log.error(String.format("0xC4A34 Invalid initialization of the derived attribute %s. Derived attributes may not be initialized.", attr.getName()),
              attr.get_SourcePositionStart());
        }
      }
    }
  }
}
