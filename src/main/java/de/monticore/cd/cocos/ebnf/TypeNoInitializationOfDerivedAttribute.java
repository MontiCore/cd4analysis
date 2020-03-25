/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that derived attributes are not initialized.
 *
 */
public class TypeNoInitializationOfDerivedAttribute implements CD4AnalysisASTCDAttributeCoCo {
  
  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.isPresentModifier()) {
      if (attr.getModifier().isDerived()) {
        if (attr.isPresentValue()) {
          Log.error(String.format("0xC4A34 Invalid initialization of the derived attribute %s. Derived attributes may not be initialized.", attr.getName()),
              attr.get_SourcePositionStart());
        }
      }
    }
  }
}
