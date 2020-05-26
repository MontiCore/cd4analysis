/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that generics are not nested.
 *
 */
public class GenericsNotNested implements CD4AnalysisASTCDAttributeCoCo {
  
  @Override
  public void check(ASTCDAttribute attr) {
    ASTMCType type = attr.getMCType();
    String typeStr = type.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()));
    if (typeStr.matches("(.*)<(.*)<(.*)")) {
      Log.error(String.format(
          "0xC4A29 Invalid type parameter %s. Generic types may not be nested.", typeStr),
          type.get_SourcePositionStart());
    }
  }
}
